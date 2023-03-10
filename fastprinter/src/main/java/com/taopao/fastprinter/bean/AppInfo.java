package com.taopao.fastprinter.bean;

import com.google.gson.annotations.SerializedName;

public class AppInfo {
    @SerializedName("name")
    private String name;
    @SerializedName("version")
    private String version;
    @SerializedName("changelog")
    private String changelog;
    @SerializedName("updated_at")
    private Integer updatedAt;
    @SerializedName("versionShort")
    private String versionShort;
    @SerializedName("build")
    private String build;
    @SerializedName("installUrl")
    private String installUrl;
    @SerializedName("direct_install_url")
    private String directInstallUrl;
    @SerializedName("update_url")
    private String updateUrl;
    @SerializedName("binary")
    private BinaryDTO binary;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getChangelog() {
        return changelog;
    }

    public void setChangelog(String changelog) {
        this.changelog = changelog;
    }

    public Integer getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Integer updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getVersionShort() {
        return versionShort;
    }

    public void setVersionShort(String versionShort) {
        this.versionShort = versionShort;
    }

    public String getBuild() {
        return build;
    }

    public void setBuild(String build) {
        this.build = build;
    }

    public String getInstallUrl() {
        return installUrl;
    }

    public void setInstallUrl(String installUrl) {
        this.installUrl = installUrl;
    }


    public String getDirectInstallUrl() {
        return directInstallUrl;
    }

    public void setDirectInstallUrl(String directInstallUrl) {
        this.directInstallUrl = directInstallUrl;
    }

    public String getUpdateUrl() {
        return updateUrl;
    }

    public void setUpdateUrl(String updateUrl) {
        this.updateUrl = updateUrl;
    }

    public BinaryDTO getBinary() {
        return binary;
    }

    public void setBinary(BinaryDTO binary) {
        this.binary = binary;
    }

    public static class BinaryDTO {
        @SerializedName("fsize")
        private Integer fsize;

        public Integer getFsize() {
            return fsize;
        }
        public void setFsize(Integer fsize) {
            this.fsize = fsize;
        }
    }
}
