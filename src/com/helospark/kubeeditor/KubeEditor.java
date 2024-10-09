/*******************************************************************************
 * Copyright (c) 2015 Øystein Idema Torget and others
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Øystein Idema Torget and others
 *******************************************************************************/
package com.helospark.kubeeditor;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.dadacoalition.yedit.YEditLog;
import org.dadacoalition.yedit.editor.YEdit;
import org.dadacoalition.yedit.editor.YEditSourceViewerConfiguration;
import org.dadacoalition.yedit.preferences.PreferenceConstants;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.IDocument;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.PartInitException;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.nodes.MappingNode;
import org.yaml.snakeyaml.nodes.Node;
import org.yaml.snakeyaml.nodes.NodeTuple;
import org.yaml.snakeyaml.nodes.ScalarNode;
import org.yaml.snakeyaml.nodes.SequenceNode;

import com.helospark.kubeeditor.validator.StaticValidatorList;
import com.helospark.kubeeditor.valueprovider.StaticValueProviderList;

import io.swagger.v3.oas.models.media.Schema;

public class KubeEditor extends YEdit {

    @Override
    protected void initializeEditor() {
        super.initializeEditor();
    }

    @Override
    protected YEditSourceViewerConfiguration createSourceViewerConfiguration() {
        return new KubeEditSourceViewerConfiguration();
    }

    @Override
    public void init(IEditorSite site, IEditorInput input) throws PartInitException {
        super.init(site, input);
        markErrors();
    }

    @Override
    protected void markErrors() {
        super.markErrors();

        try {
            IEditorInput editorInput = this.getEditorInput();

            //if the file is not part of a workspace it does not seems that it is a IFileEditorInput
            //but instead a FileStoreEditorInput. Unclear if markers are valid for such files.
            if (!(editorInput instanceof IFileEditorInput)) {
                YEditLog.logError("Marking errors not supported for files outside of a project.");
                YEditLog.logger.info("editorInput is not a part of a project.");
                return;
            }

            IFile file = ((IFileEditorInput) editorInput).getFile();
            IPreferenceStore prefs = KubeEditorActivator.getDefault().getPreferenceStore();

            String severity = prefs.getString(PreferenceConstants.VALIDATION);
            if (PreferenceConstants.SYNTAX_VALIDATION_IGNORE.equals(severity)) {
                YEditLog.logger.info("Possible syntax errors ignored due to preference settings");
                return;
            }

            int markerSeverity = IMarker.SEVERITY_ERROR;

            if (PreferenceConstants.SYNTAX_VALIDATION_WARNING.equals(severity))
                markerSeverity = IMarker.SEVERITY_WARNING;

            Map<Integer, List<String>> errors = getErrors();

            for (Map.Entry<Integer, List<String>> entry : errors.entrySet()) {
                if (entry.getValue().size() > 0) {
                    IMarker marker;
                    try {
                        marker = file.createMarker(IMarker.PROBLEM);
                        marker.setAttribute(IMarker.SEVERITY, markerSeverity);
                        marker.setAttribute(IMarker.MESSAGE, entry.getValue().stream().collect(Collectors.joining("\n")));
                        marker.setAttribute(IMarker.LINE_NUMBER, entry.getKey());
                    } catch (CoreException e) {
                        e.printStackTrace();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private Map<Integer, List<String>> getErrors() {
        IDocument document = this.getDocumentProvider().getDocument(this.getEditorInput());
        String content = document.get();

        Yaml yamlParser = new Yaml();
        Map<Integer, List<String>> result = new HashMap<>();
        for (Node element : yamlParser.composeAll(new StringReader(content))) {
            putAll(result, (getErrorsFromDocument(element)));
        }

        return result;
    }

    private Map<Integer, List<String>> getErrorsFromDocument(Node element) {
        MappingNode mappingElement = (MappingNode) element;
        Optional<String> apiVersion = findScalarElement(mappingElement, "apiVersion");
        Optional<String> kind = findScalarElement(mappingElement, "kind");

        List<String> path = new ArrayList<>();

        return recursiveGetErrorsFromDocument(element, path, apiVersion, kind);
    }

    private Map<Integer, List<String>> recursiveGetErrorsFromDocument(Node element, List<String> path, Optional<String> apiVersion, Optional<String> kind) {
        Map<Integer, List<String>> result = new HashMap<>();
        if (element instanceof ScalarNode) {
            Optional<Schema> value = YamlTools.findSchemaForPath(path, apiVersion, kind);
            if (value.isPresent()) {
                List<String> errorMessage = getErrorMessage(value.get().getType(), ((ScalarNode) element), path);

                return Collections.singletonMap(element.getStartMark().getLine() + 1, errorMessage);
            }
        } else if (element instanceof MappingNode) {
            for (NodeTuple a : ((MappingNode) element).getValue()) {
                if (a.getKeyNode() instanceof ScalarNode) {
                    path.add(((ScalarNode) a.getKeyNode()).getValue());

                    putAll(result, (recursiveGetErrorsFromDocument(a.getValueNode(), path, apiVersion, kind)));

                    path.remove(path.size() - 1);
                } else {
                    putAll(result, (recursiveGetErrorsFromDocument(a.getValueNode(), path, apiVersion, kind)));
                }
            }
        } else if (element instanceof SequenceNode) {
            for (Node a : ((SequenceNode) element).getValue()) {
                if (a instanceof ScalarNode) {
                    path.add((((ScalarNode) a).getValue()));

                    putAll(result, (recursiveGetErrorsFromDocument(a, path, apiVersion, kind)));

                    path.remove(path.size() - 1);
                } else {
                    putAll(result, (recursiveGetErrorsFromDocument(a, path, apiVersion, kind)));
                }
            }
        }

        return result;
    }

    private void putAll(Map<Integer, List<String>> result, Map<Integer, List<String>> map) {
        for (Map.Entry<Integer, List<String>> entry : map.entrySet()) {
            List<String> toUpdate = result.get(entry.getKey());
            if (toUpdate == null) {
                result.put(entry.getKey(), new ArrayList<>(entry.getValue()));
            } else {
                toUpdate.addAll(entry.getValue());
            }
        }
    }

    private List<String> getErrorMessage(String type, ScalarNode scalarNode, List<String> path) {
        List<String> validationMessages = new ArrayList<>(StaticValidatorList.getValidationMessage(type, scalarNode, path));

        List<String> validValuesForField = StaticValueProviderList.validValues(path, Optional.empty());
        String currentValue = scalarNode.getValue();
        Boolean isValid = validValuesForField.stream()
                .filter(a -> a.equals(currentValue))
                .findFirst()
                .map(a -> true)
                .orElse(false);
        if (!isValid && !validValuesForField.isEmpty()) {
            validationMessages.add("Invalid value, list of valid values: " + validValuesForField);
        }

        return validationMessages;
    }

    private Optional<String> findScalarElement(MappingNode mappingElement, String string) {
        return mappingElement.getValue()
                .stream()
                .filter(a -> a.getKeyNode() instanceof ScalarNode)
                .filter(a -> ((ScalarNode) a.getKeyNode()).getValue().equals(string))
                .map(a -> ((ScalarNode) a.getValueNode()).getValue())
                .findFirst();
    }
}
