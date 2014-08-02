package org.iplantc.de.client.models.apps.integration;


/**
 *
 * @author jstroot
 * 
 */
public enum ArgumentValidatorType {
    IntRange,
    IntAbove,
    IntBelow,
    DoubleRange,
    DoubleAbove,
    DoubleBelow,
    NonEmptyClass,
    GenotypeName,
    FileName,
    IntBelowField,
    IntAboveField,
    ClipperData,
    MustContain,
    SelectOneCheckbox,
    Regex, // TEXT
    CharacterLimit; // TEXT
    
}
