package com.ysxsoft.deliverylocker_big.bean;

public class DoorPriceBean {


    /**
     * status : 0
     * msg : Success
     * result : {"price":{"small":"首期2小时，每小时0.02元；次期3小时，每小时0.03元；之后每小时0.04元。","middle":"首期0小时，每小时0元；次期0小时，每小时0元；之后每小时0元。","big":"首期0小时，每小时0元；次期0小时，每小时0元；之后每小时0元。","superbig":"首期0小时，每小时0元；次期0小时，每小时0元；之后每小时0元。"},"count":{"small":196,"middle":0,"big":0,"superbig":0}}
     */

    private int status;
    private String msg;
    private ResultBean result;

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public ResultBean getResult() {
        return result;
    }

    public void setResult(ResultBean result) {
        this.result = result;
    }

    public static class ResultBean {
        /**
         * price : {"small":"首期2小时，每小时0.02元；次期3小时，每小时0.03元；之后每小时0.04元。","middle":"首期0小时，每小时0元；次期0小时，每小时0元；之后每小时0元。","big":"首期0小时，每小时0元；次期0小时，每小时0元；之后每小时0元。","superbig":"首期0小时，每小时0元；次期0小时，每小时0元；之后每小时0元。"}
         * count : {"small":196,"middle":0,"big":0,"superbig":0}
         */

        private PriceBean price;
        private CountBean count;

        public PriceBean getPrice() {
            return price;
        }

        public void setPrice(PriceBean price) {
            this.price = price;
        }

        public CountBean getCount() {
            return count;
        }

        public void setCount(CountBean count) {
            this.count = count;
        }

        public static class PriceBean {
            /**
             * small : 首期2小时，每小时0.02元；次期3小时，每小时0.03元；之后每小时0.04元。
             * middle : 首期0小时，每小时0元；次期0小时，每小时0元；之后每小时0元。
             * big : 首期0小时，每小时0元；次期0小时，每小时0元；之后每小时0元。
             * superbig : 首期0小时，每小时0元；次期0小时，每小时0元；之后每小时0元。
             */

            private String small;
            private String middle;
            private String big;
            private String superbig;

            public String getSmall() {
                return small;
            }

            public void setSmall(String small) {
                this.small = small;
            }

            public String getMiddle() {
                return middle;
            }

            public void setMiddle(String middle) {
                this.middle = middle;
            }

            public String getBig() {
                return big;
            }

            public void setBig(String big) {
                this.big = big;
            }

            public String getSuperbig() {
                return superbig;
            }

            public void setSuperbig(String superbig) {
                this.superbig = superbig;
            }
        }

        public static class CountBean {
            /**
             * small : 196
             * middle : 0
             * big : 0
             * superbig : 0
             */

            private int small;
            private int middle;
            private int big;
            private int superbig;

            public int getSmall() {
                return small;
            }

            public void setSmall(int small) {
                this.small = small;
            }

            public int getMiddle() {
                return middle;
            }

            public void setMiddle(int middle) {
                this.middle = middle;
            }

            public int getBig() {
                return big;
            }

            public void setBig(int big) {
                this.big = big;
            }

            public int getSuperbig() {
                return superbig;
            }

            public void setSuperbig(int superbig) {
                this.superbig = superbig;
            }
        }
    }
}
