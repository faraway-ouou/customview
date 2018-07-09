# 自定义开关

![](http://p6urtncv1.bkt.clouddn.com/img.png)

自定义View可以分为三大类

- 组合已有控件
- 完全自定义View
- 继承已有的控件实现扩展功能

这里呢我们重点讲完全自定义View，在讲解之前我们来快速了解一下Android界面绘制流程：

​	![](http://p6urtncv1.bkt.clouddn.com/3.png)

Android界面绘制重写对应方法按照上边依次执行切记这些都是在onResume之后执行，Android界面由View和ViewGroup组成：

1. View（单纯的控件,如：EditText、Button等）绘制流程

    onMeasure() (在这个方法里指定自己的宽高) -> onDraw() (绘制自己的内容)

2. ViewGroup（如：LinearLayout等）绘制流程

    onMeasure() (指定自己的宽高, 所有子View的宽高)-> onLayout() (摆放所有子View) -> onDraw() (绘制内容)

## 接下来开始定义我们的View：

1. 写一个继承View类

   ```java
   public class SwitchView extends View{
       public SwitchView(Context context) {
           super(context);
       }
       public SwitchView(Context context, @Nullable AttributeSet attrs) {
           super(context, attrs);

       }

       public SwitchView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
           super(context, attrs, defStyleAttr);
       }
   }
   ```

   我们在继承时可以看到有四个构造方法：	

   ```java
   SwitchView(Context context)  用于代码创建控件
   SwitchView(Context context, @Nullable AttributeSet attrs)  用于在xml里使用，可指定自定义属性
   SwitchView(Context context, @Nullable AttributeSet attrs, int defStyleAttr)  用于xml里使用，可指定自定义属性、和样式
   SwitchView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes)
   ```

   这里我们只需要使用三个构造方法即可。

2. 拷贝包含包名的全路径到xml中并在界面中找到该控件设置对应信息

   - 找到控件

     这个都很简单直接用findViewById就可以直接找到就不多说了


   - 设置信息

     ![](http://p6urtncv1.bkt.clouddn.com/img.png)

     上边是我们要实现的效果 可以看出只需要配置三种信息即可：设置背景图片、前景图片和开关状态

     ```java
      switchView.setSwitchBackground(R.drawable.switch_background);
      switchView.setSwitchSrc(R.drawable.slide_button);
      switchView.setSwitchOpen(true);
     ```

     我们在继承View的类中创建对应的方法

     ```java
      /**
          * 设置滑块图片
          * @param switch_background
          */
         public void setSwitchSrc(int switch_background) {
             imgSrc = BitmapFactory.decodeResource(getResources(), switch_background);

         }

         /**
          * 设置背景
          * @param switch_background
          */
         public void setSwitchBackground(int switch_background) {
             imgBg = BitmapFactory.decodeResource(getResources(),switch_background);
         }

         public void setSwitchOpen(boolean isOpen) {
             this.isOpen = isOpen;
         }
     ```

     这里我们是通过上边我们自己传入的默认开关状态设置滑块位置，left是当前控件左上角坐标，使用背景宽度-滑块宽度刚好就是左上角坐标位置。大家自己也可以看到我们的UI现在画好了，接下来我们来实现触摸反馈。

3. 设置控件的宽度和高度（测量）

   ```java
    @Override
       protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
           //设置宽度和高度
           setMeasuredDimension(imgBg.getWidth(),imgBg.getHeight());
       }
   ```

4. 然后我们类中重写onDraw方法（绘制）

   ```java
   //绘制背景
   canvas.drawBitmap(imgBg,0,0,paint);
   //绘制滑块
   if (isOpen) 
   {
       //滑块滑动位置大小
       int left = imgBg.getWidth() - imgSrc.getWidth();
       canvas.drawBitmap(imgSrc, left, 0, paint);
     } else {
       canvas.drawBitmap(imgSrc, 0, 0, paint);
      }
   ```

5. 点击事件（触摸反馈）

   ```java
    @Override
       public boolean onTouchEvent(MotionEvent event) {
           switch (event.getAction()){
               case MotionEvent.ACTION_DOWN:
                   //记录当前位置
                   switchState = true;
                   currentX = event.getX();
                   break;
               case MotionEvent.ACTION_MOVE:
                   currentX = event.getX();
                   break;
               case MotionEvent.ACTION_UP:
                   switchState = false;
                   currentX = event.getX();
                   break;
               default:
                   break;
           }
           //重新绘制界面
           invalidate();
           return true;
       }
   ```

   这里我们使用currentX 记录当前位置，并且给了一个boolean值记录了状态，然后在onDraw中重新写一下逻辑

   ```java
    if (switchState){
               canvas.drawBitmap(imgSrc, currentX, 0, paint);
           }else {
               if (isOpen) {
                   int left = imgBg.getWidth() - imgSrc.getWidth();
                   canvas.drawBitmap(imgSrc, left, 0, paint);
               } else {
                   canvas.drawBitmap(imgSrc, 0, 0, paint);
               }
           }
   ```

   这时我们就可以滑动了，这时我们发现滑块可以一直往左边滑动一直往右边滑动，这时我们则需要控制滑块位置避免滑出背景，上述代码第二行上加入：

   ```java
   			// 根据当前用户触摸到的位置画滑块
   			// 让滑块向左移动自身一半大小的位置
   			float newLeft = currentX - slideButtonBitmap.getWidth() / 2.0f;
   			
   			int maxLeft = switchBackgroupBitmap.getWidth() - slideButtonBitmap.getWidth();
   			
   			// 限定滑块范围
   			if(newLeft < 0){
   				newLeft = 0; // 左边范围
   			}else if (newLeft > maxLeft) {
   				newLeft = maxLeft; // 右边范围
   			}
   			
   			canvas.drawBitmap(slideButtonBitmap, newLeft, 0, paint);
   ```

   控制滑块滑动结束后的位置：

   ```java
   float center = switchBackgroupBitmap.getWidth() / 2.0f;
   // 根据当前按下的位置, 和控件中心的位置进行比较. 
   boolean state = currentX > center;
   // 如果开关状态变化了, 通知界面. 里边开关状态更新了.
   if(state != mSwitchState && onSwitchStateUpdateListener != null){
   	// 把最新的boolean, 状态传出去了
   	onSwitchStateUpdateListener.onStateUpdate(state);
   	}
   mSwitchState = state;
   ```

6. 自定义监听

   - 声明接口对象

     ```java
     public interface OnSwitchUpdateListener{
             void onSwitchUpdateListener(boolean state);
         }
     ```


   - 添加设置接口对象方法

     ```java
     private OnSwitchUpdateListener onSwitchUpdateListener = null;
     public void setOnSwitchUpdateListener(OnSwitchUpdateListener onSwitchUpdateListener) {
             this.onSwitchUpdateListener = onSwitchUpdateListener;
         }
     ```


   - 在适合的位置执行接口方法（这里因为是自己自定义View我们在触摸方法总执行）

     ```java
     case MotionEvent.ACTION_UP:
          switchState = false;
          currentX = event.getX();
          float center = imgBg.getWidth() / 2.0f;
          //记录变化
          boolean state = currentX > center;
          //设置监听
          if (isOpen != state && onSwitchUpdateListener !=null) {
          	 onSwitchUpdateListener.onSwitchUpdateListener(state);
              }
           isOpen = state;
     	       break;
     ```


   - 界面收到事件

     ```java
      switchView.setOnSwitchUpdateListener(new SwitchView.OnSwitchUpdateListener() {
                 @Override
                 public void onSwitchUpdateListener(boolean state) {
                     Log.e(TAG,state+"---->");
                 }
             });
     ```

7. 自定义属性（通过这个设置我们就可以直接在布局文件中直接赋值了）

   - 创建attrs并在attrs.xml声明节点declare-styleable（参照系统View的attrs）

     属性解释

     |  名称  | 说明 |            例            |
     | :----: | :--: | :----------------------: |
     |  name  | 名称 | name="switch_background" |
     | format | 类型 |    format="reference"    |

     format属性说明

     |   名称    |           说明           |
     | :-------: | :----------------------: |
     | reference | 表示引用，参考某一资源ID |
     |  string   |          字符串          |
     |   color   |          颜色值          |
     | dimension |           尺寸           |
     |  boolean  |           布尔           |
     |  integer  |           整型           |
     |   float   |          浮点数          |
     | fraction  |          百分数          |
     |   enum    |           枚举           |
     |   flag    |        表示位运算        |

     例：

     ```xml
     <?xml version="1.0" encoding="utf-8"?>
     <resources>
         <declare-styleable name="SwitchView">
             <attr name="switch_background" format="reference"/>
             <attr name="switch_src" format="reference"/>
             <attr name="switch_state" format="boolean"/>
         </declare-styleable>
     </resources>
     ```

   - 在xml配置声明的属性，添加命名空间

     命名空间声明：

     ```xml
     xmlns:android="http://schemas.android.com/apk/res/android"
     ```

     参照系统格式我总结出格式：

     ```xml
     xmlns:自己随意写="http://schemas.android.com/apk/res/项目包名"
     ```

   - 在构造函数中获取并使用

     ```java
      public SwitchView(Context context, @Nullable AttributeSet attrs) {
             super(context, attrs);
             //声明命名空间
     		String name = "http://schemas.android.com/apk/res/com.example.administrator.customview";
             isOpen = attrs.getAttributeBooleanValue(name,"switch_state",false);
             setSwitchOpen(isOpen);
             int background = attrs.getAttributeResourceValue(name,"switch_background",-1);
             setSwitchBackground(background);
             int src = attrs.getAttributeResourceValue(name,"switch_src",-1);
             setSwitchSrc(src);

         }
     ```

   这样我们的开关就大功告成啦

   ##### 	项目地址：	[https://github.com/HOUJINZHI/customview.git](https://github.com/HOUJINZHI/customview.git)

   总结：
     从上边我们写的看起来也不是很复杂哈，自定义View其实就三个重点测量、绘制和触摸反馈，我的讲解就结束啦，希望通过这篇文章能给你带来帮助。



> #### 最后献上几篇比较好的自定义View教程

- http://www.gcssloop.com/customview/CustomViewIndex
- https://blog.csdn.net/harvic880925/article/details/50995268 
- http://hencoder.com
