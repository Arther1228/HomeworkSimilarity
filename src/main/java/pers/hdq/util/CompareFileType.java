package pers.hdq.util;

public enum CompareFileType {

        EXCEL("Excel"),
        WORD_TXT("Word/Txt");

        private String name;

        CompareFileType(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }
