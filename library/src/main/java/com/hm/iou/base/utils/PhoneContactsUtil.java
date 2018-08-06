package com.hm.iou.base.utils;

import android.app.Activity;
import android.content.Intent;
import android.provider.ContactsContract;

/**
 * Created by syl on 2018/8/6.
 * 手机通讯录
 */

public class PhoneContactsUtil {

    /**
     * 打开手机通讯录，显示联系人的电话号码列表，即使同一姓名下多个号码也都显示出来
     *
     * @param activity
     * @param requestCode
     */
    public static void openPhoneContactsByNumber(final Activity activity, final int requestCode) {
        Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.CommonDataKinds.Phone.CONTENT_URI);
        activity.startActivityForResult(intent, requestCode);
    }

    /**
     * 打开手机通讯录，显示联系人列表,但是并不显示号码，因此如果传这个值的话
     * ，如果一个姓名下有多个号码的话，我们需要在onActivityResult()方法中自己取到所有号码。
     *
     * @param activity
     * @param requestCode
     */
    public static void openPhoneContactsByName(final Activity activity, final int requestCode) {
        Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
        activity.startActivityForResult(intent, requestCode);
    }

    /**
     * 打开手机通讯录，显示联系人的邮政地址列表
     *
     * @param activity
     * @param requestCode
     */
    public static void openPhoneContactsByEmailAddress(final Activity activity, final int requestCode) {
        Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.CommonDataKinds.StructuredPostal.CONTENT_URI);
        activity.startActivityForResult(intent, requestCode);
    }

    /**
     * 打开手机通讯录，显示原始联系人的电子邮件地址列表
     *
     * @param activity
     * @param requestCode
     */
    public static void openPhoneContactsByElecEmail(final Activity activity, final int requestCode) {
        Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.CommonDataKinds.Email.CONTENT_URI);
        activity.startActivityForResult(intent, requestCode);
    }


}
