package com.hm.iou.base.photo;

import android.app.Dialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.StyleRes;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import com.hm.iou.base.R;
import com.hm.iou.tools.ImageLoader;

public class SelectPicDialog extends Dialog {

    public interface OnSelectListener {
        void onDelete();

        void onReSelect();
    }

    private SelectPicDialog(@NonNull Context context, @StyleRes int themeResId) {
        super(context, themeResId);
    }

    public static SelectPicDialog createDialog(Context context, String picPath, final OnSelectListener onSelectListener) {
        final SelectPicDialog dialog = new SelectPicDialog(context, R.style.UikitAlertDialogStyle);
        // 获取Dialog布局
        View view = LayoutInflater.from(context).inflate(R.layout.base_dialog_select_pic, null);
        ImageView photoView = view.findViewById(R.id.photoView);
        ImageLoader.getInstance(context).displayImage(picPath, photoView);
        view.findViewById(R.id.iv_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        view.findViewById(R.id.tv_delete).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                onSelectListener.onDelete();
            }
        });
        view.findViewById(R.id.tv_reelect).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                onSelectListener.onReSelect();
            }
        });

        View dividerView = view.findViewById(R.id.view_divider);
        View bottomLayout = view.findViewById(R.id.ll_pic_bottom);
        if (onSelectListener == null) {
            dividerView.setVisibility(View.GONE);
            bottomLayout.setVisibility(View.GONE);
        }

        // 定义Dialog布局和参数
        dialog.setContentView(view);
        Window dialogWindow = dialog.getWindow();
        dialogWindow.setGravity(Gravity.LEFT | Gravity.BOTTOM);
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        lp.x = 0;
        lp.y = 0;
        dialogWindow.setAttributes(lp);

        //获取设备的宽度
        DisplayMetrics dm = context.getResources().getDisplayMetrics();
        // 设置Dialog最小宽度为屏幕宽度，最小高度为屏幕高度
        view.setMinimumWidth(dm.widthPixels);
        view.setMinimumHeight(dm.heightPixels);
        dialog.setCanceledOnTouchOutside(false);
        return dialog;
    }

}
