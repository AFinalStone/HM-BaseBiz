package com.hm.iou.base.demo;

import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.hm.iou.base.utils.PhoneContactsUtil;
import com.hm.iou.tools.ToastUtil;

public class ContactsActivity extends AppCompatActivity {

    private static final int REQ_OPEN_CONTACT = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts);
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_openContactsByNumber:
                PhoneContactsUtil.openPhoneContactsByNumber(this, REQ_OPEN_CONTACT);
                break;
            case R.id.btn_openContactsByName:
                PhoneContactsUtil.openPhoneContactsByName(this, REQ_OPEN_CONTACT);
                break;
            case R.id.btn_openContactsByEmailAddress:
                PhoneContactsUtil.openPhoneContactsByEmailAddress(this, REQ_OPEN_CONTACT);
                break;
            case R.id.btn_openContactsByElecEmail:
                PhoneContactsUtil.openPhoneContactsByElecEmail(this, REQ_OPEN_CONTACT);
                break;
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQ_OPEN_CONTACT) {
            if (resultCode == RESULT_OK) {
                if (data != null) {
                    Uri uri = data.getData();
                    String[] contact = getPhoneContacts(uri);
                    if (contact != null) {
                        if (contact[0] != null) {
                            String name = contact[0];//姓名
                            Log.i("联系人名称", name);
                            ToastUtil.showMessage(this, "联系人名称：" + name);
                        }
                        if (contact[1] != null) {
                            String number = contact[1];//手机号
                            Log.i("联系人手机号", number);
                            ToastUtil.showMessage(this, "联系人手机号：" + number);
                        }
                    }
                }
            }
        }
    }

    /**
     * 读取联系人信息
     *
     * @param uri
     */
    private String[] getPhoneContacts(Uri uri) {
        String[] contact = new String[2];
        //得到ContentResolver对象
        try {
            ContentResolver cr = getContentResolver();
            Cursor cursor = cr.query(uri, null, null, null, null);
            if (cursor != null && cursor.moveToFirst()) {
                //取得联系人姓名
                int nameFieldColumnIndex = cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME);
                contact[0] = cursor.getString(nameFieldColumnIndex);
                contact[1] = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                cursor.close();
            }
        } catch (Exception e) {

        }
        return contact;
    }

//    private void getContactsName(Uri uri) {
//        Cursor c = managedQuery(uri, null, null, null, null);
//        if (c.moveToFirst()) {
//            String name = c.getString(c.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
//            String hasPhone = c.getString(c.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER));
//            String contactId = c.getString(c.getColumnIndex(ContactsContract.Contacts._ID));
//            String phoneNumber = null;
//            if (hasPhone.equalsIgnoreCase("1")) {
//                hasPhone = "true";
//            } else {
//                hasPhone = "false";
//            }
//            if (Boolean.parseBoolean(hasPhone)) {
//                Cursor phones = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
//                        null,
//                        ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = "
//                                + contactId,
//                        null,
//                        null);
//                while (phones.moveToNext()) {
//                    phoneNumber = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
//                    ToastUtil.showMessage(ContactsActivity.this, phoneNumber);
//                }
//                phones.close();
//            }
//
//
//        }
//    }


}
