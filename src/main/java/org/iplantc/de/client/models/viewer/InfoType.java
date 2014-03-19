package org.iplantc.de.client.models.viewer;

public enum InfoType {

    ACE("ace"), BASH("bash"), BLAST("blast"), BOWTIE("bowtie"), CLUSTALW("clustalw"), CODATA("codata"), CSH(
            "csh"), CSV("csv"), EMBL("embl"), FASTA("fasta"), FASTQ("fastq"), FASTXY("fastxy"), GAME(
            "game"), GCG("gcg"), GCGBLAST("gcgblast"), GCGFASTA("gcgfasta"), GDE("gde"), GENBANK(
            "genbank"), GENSCAN("genscan"), GFF("gff"), HMMER("hmmer"), MASE("mase"), MEGG("mega"), MSF(
            "msf"), NEWICK("newick"), NEXML("nexml"), NEXUS("nexus"), PERL("perl"), PHYLIP("phylip"), PHYLOXML(
            "phyloxml"), PIR("pir"), PRODOM("prodom"), PYTHON("python"), RAW("raw"), RSF("rsf"), SELEX(
            "selex"), SH("sh"), STOCKHOLM("stockholm"), SWISS("swiss"), TCSH("tcsh"), TSV("tsv"), VCF(
            "vcf");

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

        return valueOf(typeString);
    }

    @Override
    public String toString() {
        return info_type;
    }

}
