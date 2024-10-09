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

import org.dadacoalition.yedit.preferences.PreferenceConstants;
import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferenceConverter;
import org.eclipse.swt.graphics.RGB;

/**
 * Class used to initialize default preference values.
 */
public class PreferenceInitializer extends AbstractPreferenceInitializer {

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer#initializeDefaultPreferences()
     */
    @Override
    public void initializeDefaultPreferences() {
        IPreferenceStore store = KubeEditorActivator.getDefault().getPreferenceStore();

        PreferenceConverter.setDefault(store, PreferenceConstants.COLOR_DEFAULT, new RGB(141, 218, 248));
        store.setDefault(PreferenceConstants.BOLD_DEFAULT, false);
        store.setDefault(PreferenceConstants.ITALIC_DEFAULT, false);
        store.setDefault(PreferenceConstants.UNDERLINE_DEFAULT, false);

        PreferenceConverter.setDefault(store, PreferenceConstants.COLOR_COMMENT, new RGB(255, 0, 50));
        store.setDefault(PreferenceConstants.BOLD_COMMENT, true);
        store.setDefault(PreferenceConstants.ITALIC_COMMENT, false);
        store.setDefault(PreferenceConstants.UNDERLINE_COMMENT, false);

        PreferenceConverter.setDefault(store, PreferenceConstants.COLOR_KEY, new RGB(0, 200, 50));
        store.setDefault(PreferenceConstants.BOLD_KEY, false);
        store.setDefault(PreferenceConstants.ITALIC_KEY, false);
        store.setDefault(PreferenceConstants.UNDERLINE_KEY, false);

        PreferenceConverter.setDefault(store, PreferenceConstants.COLOR_DOCUMENT, new RGB(141, 218, 248));
        store.setDefault(PreferenceConstants.BOLD_DOCUMENT, false);
        store.setDefault(PreferenceConstants.ITALIC_DOCUMENT, false);
        store.setDefault(PreferenceConstants.UNDERLINE_DOCUMENT, false);

        PreferenceConverter.setDefault(store, PreferenceConstants.COLOR_SCALAR, new RGB(110, 160, 190));
        store.setDefault(PreferenceConstants.BOLD_SCALAR, true);
        store.setDefault(PreferenceConstants.ITALIC_SCALAR, false);
        store.setDefault(PreferenceConstants.UNDERLINE_SCALAR, false);

        PreferenceConverter.setDefault(store, PreferenceConstants.COLOR_ANCHOR, new RGB(175, 0, 255));
        store.setDefault(PreferenceConstants.BOLD_ANCHOR, false);
        store.setDefault(PreferenceConstants.ITALIC_ANCHOR, false);
        store.setDefault(PreferenceConstants.UNDERLINE_ANCHOR, false);

        PreferenceConverter.setDefault(store, PreferenceConstants.COLOR_ALIAS, new RGB(175, 0, 255));
        store.setDefault(PreferenceConstants.BOLD_ALIAS, false);
        store.setDefault(PreferenceConstants.ITALIC_ALIAS, false);
        store.setDefault(PreferenceConstants.UNDERLINE_ALIAS, false);

        PreferenceConverter.setDefault(store, PreferenceConstants.COLOR_TAG_PROPERTY, new RGB(175, 0, 255));
        store.setDefault(PreferenceConstants.BOLD_TAG_PROPERTY, false);
        store.setDefault(PreferenceConstants.ITALIC_TAG_PROPERTY, false);
        store.setDefault(PreferenceConstants.UNDERLINE_TAG_PROPERTY, false);

        PreferenceConverter.setDefault(store, PreferenceConstants.COLOR_INDICATOR_CHARACTER, new RGB(141, 218, 248));
        store.setDefault(PreferenceConstants.BOLD_INDICATOR_CHARACTER, false);
        store.setDefault(PreferenceConstants.ITALIC_INDICATOR_CHARACTER, false);
        store.setDefault(PreferenceConstants.UNDERLINE_INDICATOR_CHARACTER, false);

        PreferenceConverter.setDefault(store, PreferenceConstants.COLOR_CONSTANT, new RGB(200, 93, 39));
        store.setDefault(PreferenceConstants.BOLD_CONSTANT, true);
        store.setDefault(PreferenceConstants.ITALIC_CONSTANT, false);
        store.setDefault(PreferenceConstants.UNDERLINE_CONSTANT, false);

        store.setDefault(PreferenceConstants.SPACES_PER_TAB, 2);
        store.setDefault(PreferenceConstants.SECONDS_TO_REEVALUATE, 3);

        store.setDefault(PreferenceConstants.OUTLINE_SCALAR_MAX_LENGTH, 30);
        store.setDefault(PreferenceConstants.OUTLINE_SHOW_TAGS, true);

        store.setDefault(PreferenceConstants.SYMFONY_COMPATIBILITY_MODE, false);

        store.setDefault(PreferenceConstants.AUTO_EXPAND_OUTLINE, true);
        store.setDefault(PreferenceConstants.VALIDATION, PreferenceConstants.SYNTAX_VALIDATION_ERROR);

        store.setDefault(PreferenceConstants.FORMATTER_EXPLICIT_END, false);
        store.setDefault(PreferenceConstants.FORMATTER_EXPLICIT_START, false);
        store.setDefault(PreferenceConstants.FORMATTER_FLOW_STYLE, "BLOCK");
        store.setDefault(PreferenceConstants.FORMATTER_SCALAR_STYLE, "PLAIN");
        store.setDefault(PreferenceConstants.FORMATTER_PRETTY_FLOW, true);
        store.setDefault(PreferenceConstants.FORMATTER_LINE_WIDTH, 80);

        store.setDefault(PreferenceConstants.TODO_TASK_TAGS, "FIXME,TODO");
        store.setDefault(PreferenceConstants.TODO_TASK_PRIORITIES, "high,normal");
        store.setDefault(PreferenceConstants.TODO_TASK_CASE_SENSITIVE, false);
    }

}
