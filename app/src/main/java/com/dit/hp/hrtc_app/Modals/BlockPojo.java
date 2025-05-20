package com.dit.hp.hrtc_app.Modals;

import java.io.Serializable;

public class BlockPojo implements Serializable {

        private int blockId;
        private String blockName;

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

