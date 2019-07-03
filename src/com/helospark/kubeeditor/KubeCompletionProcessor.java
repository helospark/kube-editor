package com.helospark.kubeeditor;

import static com.helospark.kubeeditor.YamlTools.getPath;
import static com.helospark.kubeeditor.YamlTools.isAfterColon;
import static com.helospark.kubeeditor.YamlTools.isComment;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.contentassist.CompletionProposal;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.templates.Template;
import org.eclipse.jface.text.templates.TemplateCompletionProcessor;
import org.eclipse.jface.text.templates.TemplateContextType;
import org.eclipse.swt.graphics.Image;

import com.helospark.kubeeditor.valueprovider.StaticValueProviderList;

import io.swagger.v3.oas.models.media.ArraySchema;
import io.swagger.v3.oas.models.media.MapSchema;
import io.swagger.v3.oas.models.media.Schema;

public class KubeCompletionProcessor extends TemplateCompletionProcessor {

    @Override
    public ICompletionProposal[] computeCompletionProposals(ITextViewer viewer, int offsetInput) {
        List<ICompletionProposal> result = new ArrayList<>();

        String content = viewer.getDocument().get();

        int offset;
        if (offsetInput >= content.length()) {
            offset = content.length() - 1;
        } else {
            offset = offsetInput - 1;
        }

        // Not really a perfect solution (ex. multiline not handled), but will be good enough here 

        if (isComment(content, offset)) {
            return new ICompletionProposal[0];
        }

        boolean isValue = isAfterColon(content, offset);

        List<String> path = getPath(content, offset);

        Optional<String> apiVersion = YamlTools.findApiVersion(content, offset);
        Optional<String> kind = YamlTools.findKind(content, offset);

        if (YamlTools.isEmptyLine(content, offset)) {
            result.add(createTemplateCustom(offset, "Create deployment", "deployment-template.yaml", YamlTools.getCurrentLine(content, offset), content));
            result.add(createTemplateCustom(offset, "Service deployment", "service-template.yaml", YamlTools.getCurrentLine(content, offset), content));
            result.add(createTemplateCustom(offset, "Ingress deployment", "ingress-template.yaml", YamlTools.getCurrentLine(content, offset), content));
        }

        Optional<String> currentKey = YamlTools.currentKey(content, offset);
        Optional<String> currentValue = YamlTools.currentValue(content, offset);
        String valueOrEmptyString = currentValue.orElse("");
        Optional<Integer> currentSpaces = YamlTools.currentSpaces(content, offset);
        if (!isValue) {
            if (!apiVersion.isPresent()) {
                result.add(createProposalCustom(offset, "kind", "kind: ",
                        "Kind is a string value representing the REST resource this object represents. Servers may infer this from the endpoint the client submits requests to. Cannot be updated. In CamelCase. More info: https://git.k8s.io/community/contributors/devel/api-conventions.md#types-kinds",
                        currentKey.orElse("")));
            }
            if (!kind.isPresent()) {
                result.add(createProposalCustom(offset, "apiVersion", "apiVersion: apps/v1\n",
                        "APIVersion defines the versioned schema of this representation of an object. Servers should convert recognized schemas to the latest internal value, and may reject unrecognized values. More info: https://git.k8s.io/community/contributors/devel/api-conventions.md#resources",
                        currentKey.orElse("")));
            }
        }

        if (isValue) {
            result.addAll(StaticValueProviderList.validValues(path, currentValue)
                    .stream()
                    .map(a -> createProposalCustom(offset, a, a, a, valueOrEmptyString))
                    .collect(Collectors.toList()));
        }

        Optional<Schema> optionalSchema = YamlTools.findSchemaForPath(path, content, offset);

        if (optionalSchema.isPresent()) {
            Schema schema = optionalSchema.get();
            if (isValue) {
                String type = optionalSchema.get().getType();

                if (type.equals("boolean")) {
                    if ("true".startsWith(valueOrEmptyString)) {
                        result.add(createProposalCustom(offset, "true", " true", "true", valueOrEmptyString));
                    }
                    if ("false".startsWith(valueOrEmptyString)) {
                        result.add(createProposalCustom(offset, "false", " false", "false", valueOrEmptyString));
                    }
                }

            } else if (schema.getProperties() != null) {
                String keyPrefixToSearch = currentKey.orElse("");

                result.addAll(((Map<String, Schema>) schema.getProperties())
                        .entrySet()
                        .stream()
                        .filter(a -> a.getKey().startsWith(keyPrefixToSearch))
                        .map(a -> {
                            String postFix;
                            if (offset < content.length() - 1 && content.charAt(offset + 1) == ':') {
                                postFix = "";
                            } else if (a.getValue().get$ref() != null || a.getValue() instanceof ArraySchema || a.getValue() instanceof MapSchema) {
                                postFix = ":\n";
                                for (int i = 0; i < currentSpaces.map(b -> b + 2).orElse(2); ++i) {
                                    postFix += " ";
                                }
                            } else {
                                postFix = ": ";
                            }
                            return createProposalCustom(offset, a.getKey(), a.getKey() + postFix, a.getValue().getDescription() + "\n\nname: " + a.getKey() + "\ntype: " + a.getValue().getType(),
                                    currentKey.orElse(""));
                        })
                        .collect(Collectors.toList()));
            }
        }

        return result.toArray(new ICompletionProposal[0]);
    }

    private ICompletionProposal createTemplateCustom(final int offset, final String title, final String filename, final String whatToReplace, String content) {
        boolean beforePosition = YamlTools.isNewDocumentBeforePosition(content, offset);
        boolean afterPosition = YamlTools.isNewDocumentAfterPosition(content, offset);

        String replacement = ResourceFileReaderWithCache.getOrReadFile(filename);

        String newReplacement = replacement;
        if (!beforePosition) {
            newReplacement = "---\n" + replacement;
        }
        if (!afterPosition) {
            newReplacement = replacement + "\n---\n";
        }

        return createProposalCustom(offset, title, newReplacement, title + "\n" + replacement, whatToReplace);
    }

    private ICompletionProposal createProposalCustom(final int offset, final String title, final String replacement, final String description, final String whatToReplace) {
        return new CompletionProposal(replacement, offset - whatToReplace.length() + 1, whatToReplace.length(), replacement.length(), null, title, null, addNewLines(description));
    }

    private String addNewLines(String description) {
        String result = "";
        int wordCount = 0;

        for (int i = 0; i < description.length(); ++i) {
            char c = description.charAt(i);

            if (c == ' ') {
                ++wordCount;
            }
            if (c == '\n') {
                wordCount = 0;
            }
            if (c == ' ' && wordCount > 0 && wordCount % 7 == 0) {
                result += "\n";
            } else {
                result += c;
            }
        }

        return result;
    }

    @Override
    protected Template[] getTemplates(String var1) {
        return null;
    }

    @Override
    protected TemplateContextType getContextType(ITextViewer var1, IRegion var2) {
        return null;
    }

    @Override
    protected Image getImage(Template var1) {
        return null;
    }

}
