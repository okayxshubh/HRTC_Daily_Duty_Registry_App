package com.dit.hp.hrtc_app.Modals;

import java.io.Serializable;

public class BlockPojo implements Serializable {

        private int blockId;
        private String blockName;
        private String blockLgdCode;

        public String getBlockLgdCode() {
                return blockLgdCode;
        }

        public void setBlockLgdCode(String blockLgdCode) {
                this.blockLgdCode = blockLgdCode;
        }

        public int getBlockId() {
                return blockId;
        }

        public void setBlockId(int blockId) {
                this.blockId = blockId;
        }

        public String getBlockName() {
                return blockName;
        }

        public void setBlockName(String blockName) {
                this.blockName = blockName;
        }

        @Override
        public String toString() {
                return blockName;
        }
}

