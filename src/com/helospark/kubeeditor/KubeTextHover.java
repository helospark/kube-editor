package com.helospark.kubeeditor;

import static com.helospark.kubeeditor.YamlTools.getPath;
import static com.helospark.kubeeditor.YamlTools.isComment;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;

import org.eclipse.jface.text.DefaultInformationControl;
import org.eclipse.jface.text.IInformationControl;
import org.eclipse.jface.text.IInformationControlCreator;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextHover;
import org.eclipse.jface.text.ITextHoverExtension;
import org.eclipse.jface.text.ITextHoverExtension2;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.Region;
import org.eclipse.swt.widgets.Shell;

import io.swagger.v3.oas.models.media.Schema;

public class KubeTextHover implements ITextHover, ITextHoverExtension, ITextHoverExtension2 {

    @Override
    public String getHoverInfo(ITextViewer viewer, IRegion region) {
        return null;
    }

    @Override
    public IRegion getHoverRegion(ITextViewer viewer, int offset) {
        return new Region(offset, 0);
    }

    @Override
    public Object getHoverInfo2(ITextViewer viewer, IRegion region) {
        String content = viewer.getDocument().get();

        int offset;
        if (region.getOffset() >= content.length()) {
            offset = content.length() - 1;
        } else {
            offset = region.getOffset();
        }

        if (isComment(content, offset)) {
            return null;
        }

        int lineEnd = content.indexOf('\n', offset) - 1;
        if (lineEnd < 0) {
            lineEnd = content.length() - 1;
        }
        List<String> path = getPath(content, offset);
        Optional<Schema> optionalSchema = YamlTools.findSchemaForPath(path, content, offset);
        Optional<String> currentKey = YamlTools.currentKey(content, lineEnd);

        if (currentKey.isPresent() && optionalSchema.isPresent() && optionalSchema.get().getProperties() != null) {
            Optional<Entry<String, Schema>> key = ((Map<String, Schema>) optionalSchema.get().getProperties())
                    .entrySet()
                    .stream()
                    .filter(a -> a.getKey().equals(currentKey.get()))
                    .findFirst();
            Optional<String> result = key
                    .map(a -> a.getValue().getDescription());
            if (!result.isPresent() && key.isPresent() && key.get().getValue().get$ref() != null) {
                Schema schemaResult = SchemaParser.getApi().getComponents()
                        .getSchemas()
                        .get(key.get().getValue().get$ref().replaceFirst("#/components/schemas/", ""));
                result = Optional.ofNullable(schemaResult.getDescription());
            }
            if (result.isPresent()) {
                return "<b>" + currentKey.get() + "</b><br>" + result.get();
            }
        }
        return null;
    }

    @Override
    public IInformationControlCreator getHoverControlCreator() {
        return new IInformationControlCreator() {
            @Override
            public IInformationControl createInformationControl(Shell shell) {
                return new DefaultInformationControl(shell, true);
            }
        };
    }

}
