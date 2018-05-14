package com.yaheen.pdaapp.bean;

import java.io.Serializable;

public class MsgBean implements Serializable {

    private static final long serialVersionUID = 5602235794418727193L;
    /**
     * result : true
     * entity : {"community ":"20180502","id":"http://fanyi.youdao.com/ ","username":"张三","sex":" 男","address":" 广州天河","mobile":" 13928897740","phone":" 02013465","peoplenumber":" 2","party_member":" 是"}
     */

    private boolean result;
    private EntityBean entity;

    //实体类变量的个数
    public static int num = 20;

    public boolean isResult() {
        return result;
    }

    public void setResult(boolean result) {
        this.result = result;
    }

    public EntityBean getEntity() {
        return entity;
    }

    public void setEntity(EntityBean entity) {
        this.entity = entity;
    }

    public static class EntityBean {
        /**
         * community  : 20180502
         * id : http://fanyi.youdao.com/
         * username : 张三
         * sex :  男
         * address :  广州天河
         * mobile :  13928897740
         * phone :  02013465
         * peoplenumber :  2
         * party_member :  是
         */

        private String community;
        private String id;
        private String username;
        private String sex;
        private String address;
        private String mobile;
        private String phone;
        private String peoplenumber;
        private String party_member;

        public String getCommunity() {
            return community;
        }

        public void setCommunity(String community) {
            this.community = community;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getSex() {
            return sex;
        }

        public void setSex(String sex) {
            this.sex = sex;
        }

        public String getAddress() {
            return address;
        }

        public void setAddress(String address) {
            this.address = address;
        }

        public String getMobile() {
            return mobile;
        }

        public void setMobile(String mobile) {
            this.mobile = mobile;
        }

        public String getPhone() {
            return phone;
        }

        public void setPhone(String phone) {
            this.phone = phone;
        }

        public String getPeoplenumber() {
            return peoplenumber;
        }

        public void setPeoplenumber(String peoplenumber) {
            this.peoplenumber = peoplenumber;
        }

        public String getParty_member() {
            return party_member;
        }

        public void setParty_member(String party_member) {
            this.party_member = party_member;
        }
    }
}
