# AndroidAssistiveTouch
一款可定制的辅助控制控件，你可以自定义它的颜色，大小与控件的图标以及针对不同图标的响应事件。控件针对手势响应进行了优化。你可以自由的拖动缩放状态下的控件，
也可以双指缩放展开状态下的控件面板。

## 预览
由于录制工具的原因导致显示的动画效果不够流畅，实际使用不会出现这方面的问题
![预览图片](http://chuantu.biz/t6/320/1527509384x-1404795523.gif)

##Features
### 手势
1.缩放状态下点击按钮<br>
2.缩放状态下拖动按钮<br>
3.缩放状态下按住按钮做抛物<br>
4.展开状态下点击控制按钮外部<br>
5.展开状态下双指缩放控制面板<br>

### 定制
![属性图片](http://chuantu.biz/t6/320/1527509592x-1404758293.png)<br>
color:背景颜色<br>
original_radius:按钮的半径<br>
spread_radius:控制面板的半径<br>
icon_sourth,icon_north,icon_west,icon_east:控制面板东南西北四个方位控件的资源文件<br>
floatX:控件距离父控件顶部的相对位置<br>
floatY:控件距离父控件左部的相对位置<br>

## Quick Start
1)将view文件夹下的类文件加入你的工程<br><br>
2)将assestive_touch_view.xml加入你工程的res/values目录下<br><br>
3)在你工程的colors.xml文件中加入 <color name="colorWhite">#FFFFFF</color> <br><br>
4)在布局文件头加入自定义布局的命名空间<br><br>
xmlns:andriod_assestive_touch="http://schemas.android.com/apk/res-auto"<br>
5)在布局文件中使用自定义的控件<br><br>
    <com.example.administrator.studyanimation.view.AssestiveTouchLayout
        android:layout_width="match_parent"
        android:layout_height="match_parent">

        <com.example.administrator.studyanimation.view.AssestiveTouchDemo
            android:layout_width="0dp"
            android:layout_height="0dp"
            andriod_assestive_touch:original_radius="50"
            andriod_assestive_touch:spread_radius="200"
            andriod_assestive_touch:color="@color/colorGray"
            andriod_assestive_touch:icon_sourth="@drawable/icon6"
            andriod_assestive_touch:icon_north="@drawable/icon5"
            andriod_assestive_touch:icon_west="@drawable/icon4"
            andriod_assestive_touch:icon_east="@drawable/icon3"
            andriod_assestive_touch:floatX="200"
            andriod_assestive_touch:floatY="200"
            android:id="@+id/assestive_touch_button"/>
    </com.example.administrator.studyanimation.view.AssestiveTouchLayout>
6)在activty中加入控制面板中控件对应的逻辑<br><br>
        AssestiveTouchDemo assestiveTouchDemo=(AssestiveTouchDemo)findViewById(R.id.assestive_touch_button);
        assestiveTouchDemo.setAssestiveTouchListener(new AssestiveTouchListener() {
            @Override
            public void onNorthWigetClick() {
                Log.v("AssestiveTouch","north");
            }

            @Override
            public void onSourthWigetClick() {
                Log.v("AssestiveTouch","sourth");
            }

            @Override
            public void onWestWigetClick() {
                Log.v("AssestiveTouch","west");
            }

            @Override
            public void onEastWigetClick() {
                Log.v("AssestiveTouch","east");
            }
        });
    }

end
