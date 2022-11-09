package org.openscience.cdk.io;

import java.util.*;

public class RdfileRecord {
    private Rxnfile rxnFile;
    private Molfile molfile;
    private String internalRegistryNumber;
    private String externalRegistryNumber;
    private Map<String, String> data = new LinkedHashMap<>();

    public Rxnfile getRxnFile() {
        return rxnFile;
    }

    public void setRxnFile(Rxnfile rxnFile) {
        this.rxnFile = rxnFile;
    }

    public Molfile getMolfile() {
        return molfile;
    }

    public void setMolfile(Molfile molfile) {
        this.molfile = molfile;
    }

    public String getInternalRegistryNumber() {
        return internalRegistryNumber;
    }

    public void setInternalRegistryNumber(String internalRegistryNumber) {
        this.internalRegistryNumber = internalRegistryNumber;
    }

    public String getExternalRegistryNumber() {
        return externalRegistryNumber;
    }

    public void setExternalRegistryNumber(String externalRegistryNumber) {
        this.externalRegistryNumber = externalRegistryNumber;
    }

    public void setData(Map<String, String> data) {
        this.data = data;
    }

    public void putData(String dtype, String datum) {
        data.put(dtype, datum);
    }

    public Map<String, String> getData() {
        return Collections.unmodifiableMap(data);
    }

    public String getDatum(String dtype) {
        return data.get(dtype);
    }

    public static final class Molfile {
        private final String title;
        private final String content;

        public Molfile(String title, String content) {
            this.title = title;
            this.content = content;
        }

        public String getTitle() {
            return title;
        }

        public String getContent() {
            return content;
        }

        @Override
        public String toString() {
            return "Molfile{" +
                    "title='" + title + '\'' +
                    ", content='" + content + '\'' +
                    '}';
        }
    }

    public static final class Rxnfile {
        private final String title;
        private final String header;
        private final String remark;
        private final List<Molfile> reactants;
        private final List<Molfile> products;
        private final List<Molfile> agents;

        public Rxnfile(String title, String header, String remark, List<Molfile> reactants, List<Molfile> agents, List<Molfile> products) {
            this.title = title;
            this.header = header;
            this.remark = remark;
            this.reactants = reactants;
            this.agents = agents;
            this.products = products;
        }

        public String getTitle() {
            return title;
        }

        public String getHeader() {
            return header;
        }

        public String getRemark() {
            return remark;
        }

        public Iterable<Molfile> reactants() {
            return Collections.unmodifiableList(reactants);
        }

        public Iterable<Molfile> products() {
            return Collections.unmodifiableList(products);
        }

        public Iterable<Molfile> agents() {
            return Collections.unmodifiableList(agents);
        }

        public int getNumReactants() {
            return reactants.size();
        }

        public int getNumProducts() {
            return products.size();
        }

        public int getNumAgents() {
            return agents.size();
        }

        @Override
        public String toString() {
            return "Rxnfile{" +
                    "title='" + title + '\'' +
                    ", header='" + header + '\'' +
                    ", remark='" + remark + '\'' +
                    ", reactants=" + reactants +
                    ", products=" + products +
                    ", agents=" + agents +
                    '}';
        }
    }
}
