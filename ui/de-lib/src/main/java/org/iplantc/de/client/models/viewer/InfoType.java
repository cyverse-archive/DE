package org.iplantc.de.client.models.viewer;

public enum InfoType {

    ACE("ace"),
    BAM("bam"),
    BASH("bash"),
    BED("bed"),
    BLAST("blast"),
    BOWTIE("bowtie"),
    CLUSTALW("clustalw"),
    CODATA("codata"),
    CSH("csh"),
    CSV("csv"),
    EMBL("embl"),
    FASTA("fasta"),
    FASTQ("fastq"),
    FASTXY("fastxy"),
    GAME("game"),
    GCG("gcg"),
    GCGBLAST("gcgblast"),
    GCGFASTA("gcgfasta"),
    GDE("gde"),
    GENBANK("genbank"),
    GENSCAN("genscan"),
    GFF("gff"),
    GTF("gtf"),
    HMMER("hmmer"),
    JAR("jar"),
    MASE("mase"),
    MEGA("mega"),
    MSF("msf"),
    NEWICK("newick"),
    NEXML("nexml"),
    NEXUS("nexus"),
    PDF("pdf"),
    HT_ANALYSIS_PATH_LIST("ht-analysis-path-list"),
    PERL("perl"),
    PHYLIP("phylip"),
    PHYLOXML("phyloxml"),
    PIR("pir"),
    PRODOM("prodom"),
    PYTHON("python"),
    RAW("raw"),
    RSF("rsf"),
    SELEX("selex"),
    SFF("sff"),
    SH("sh"),
    STOCKHOLM("stockholm"),
    SWISS("swiss"),
    TCSH("tcsh"),
    TSV("tsv"),
    VCF("vcf"),
    ZIP("zip");

    private String info_type;

    private InfoType(String infoType) {
        this.info_type = infoType;
    }

    public String getTypeString() {
        return toString().toLowerCase();
    }

    /**
     * Null-safe and case insensitive variant of valueOf(String)
     * 
     * @param typeString name of an mime type constant
     * @return
     */
    public static InfoType fromTypeString(String typeString) {
        if (typeString == null || typeString.isEmpty()) {
            return null;
        }

        return valueOf(typeString.toUpperCase().replaceAll("[-.+]", "_"));
    }

    @Override
    public String toString() {
        return info_type;
    }

}
