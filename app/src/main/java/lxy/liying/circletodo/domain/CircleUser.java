package lxy.liying.circletodo.domain;

import cn.bmob.v3.BmobUser;

public class CircleUser extends BmobUser {
	private static final long serialVersionUID = 1L;
    /** 用户ID */
	private String uid;
    /** 用户头像路径 */
	private String head_icon;
    /** 数据库备份地址 */
    private String dbBackupUrl;
    /** 已使用的验证邮件重发次数 */
    private int verify_count;

    public static final String UID = "uid";
    public static final String HEAD_ICON = "head_icon";
    public static final String DB_BACKUP_URL = "dbBackupUrl";
    public static final String VERIFY_COUNT = "verify_count";

	public CircleUser() {
	}

    public CircleUser(String uid, String head_icon, String dbBackupUrl, int verify_count) {
        this.uid = uid;
        this.head_icon = head_icon;
        this.dbBackupUrl = dbBackupUrl;
        this.verify_count = verify_count;
    }

    public String getUid() {
		return uid;
	}

	public void setUid(String uid) {
		this.uid = uid;
	}

	public String getHead_icon() {
		return head_icon;
	}

	public void setHead_icon(String head_icon) {
		this.head_icon = head_icon;
	}

    public String getDbBackupUrl() {
        return dbBackupUrl;
    }

    public void setDbBackupUrl(String dbBackupUrl) {
        this.dbBackupUrl = dbBackupUrl;
    }

    public int getVerify_count() {
        return verify_count;
    }

    public void setVerify_count(int verify_count) {
        this.verify_count = verify_count;
    }

    @Override
    public String toString() {
        return "CircleUser{" +
                "uid='" + uid + '\'' +
                ", head_icon='" + head_icon + '\'' +
                ", dbBackupUrl='" + dbBackupUrl + '\'' +
                ", verify_count=" + verify_count +
                '}';
    }
}
