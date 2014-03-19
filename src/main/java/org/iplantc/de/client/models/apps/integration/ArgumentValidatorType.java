package org.iplantc.de.client.models.apps.integration;


/**
 * FIXME JDS This needs to have a corresponding Label for each validator.
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
