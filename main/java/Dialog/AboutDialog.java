package Dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import androidx.fragment.app.DialogFragment;
import com.sample.thehealthscale.R;

public class AboutDialog extends DialogFragment {

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity())
                .setTitle(getString(R.string.about));
        builder.setMessage("该系统是健康管理系统，用户首次登录后设置个人信息，下载食物数据，以便后续的使用。\n" +
                "本系统可以根据用户个人信息计算每日应摄入的热量，并予对用户已经摄入的热量以显示。\n" +
                "用户可在饮食页面对饮食记录进行打卡，打卡前请先连接硬件端蓝牙。连接后点击‘去打卡’进行饮食的记录。\n" +
                "对饮食记录打卡后在饮食页面可以显示用户摄入的营养成分比例，来对用户进行饮食的提醒。");
        //builder.setCancelable(false);
        builder.setIcon(R.drawable.warning);
        builder.setPositiveButton(getString(R.string.setting_submit), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        })
                .setNegativeButton(getString(R.string.setting_cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
        return builder.create();
    }
}
