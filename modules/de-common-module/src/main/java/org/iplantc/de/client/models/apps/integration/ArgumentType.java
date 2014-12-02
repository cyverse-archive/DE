package org.iplantc.de.client.models.apps.integration;

/**
 * @author jstroot
 */
public enum ArgumentType {
    Input,
    FileInput,
    FolderInput,
    MultiFileSelector,
    FileFolderInput,
    EnvironmentVariable,
    Flag,
    Info,
    MultiLineText,
    Integer,
    Double,
    Text,
    TextSelection, // For selecting from a list of string values.
    IntegerSelection, // For selecting from a list of integers
    DoubleSelection, // For selecting from a list of doubles
    TreeSelection,
    Output,
    FileOutput, FolderOutput, MultiFileOutput,
    ReferenceGenome,
    ReferenceSequence, 
    ReferenceAnnotation,
    // Legacy
    Selection, 
    ValueSelection,
    Number, 
    Group;
    // Input
    // Output

}
