# android-drag-FowLayout
this is a draggable flow layout lib.

 <img src="/art/drag_flowlayout.gif" alt="Demo Screen Capture" width="296px" height="581px" />

## 特点
 - 1, 类似可拖拽的GridView. 不过gridView 宽度/个数是固定的。 这个布局item宽度是不定的。
 - 2，长按item拖拽，如果要处理点击事件请调用。
```java
  mDragflowLayout.setOnItemClickListener(new DragFlowLayout.OnItemClickListener() {
            @Override
            public boolean performClick(DragFlowLayout dragFlowLayout, View child,
                                        MotionEvent event, int dragState) {
                //检查是否点击了关闭按钮(iv_close控件)。点击了就删除
                //ViewUtils.isViewUnderInScreen 判断点击事件是否是你需要的.
                //dragState 是拖拽状态。
                boolean performed = dragState != DragFlowLayout.DRAG_STATE_IDLE
                        && ViewUtils.isViewUnderInScreen(child.findViewById(R.id.iv_close),
                        (int) event.getRawX(),(int) event.getRawY());
                if(performed){
                    dragFlowLayout.removeView(child);
                }
                //点击事件
                return performed;
            }
        });
```
 - 3，可嵌套ScrollerView.  demo就是。
 - 4, 如果想禁止某些Item拖拽请实现 {@link IDraggable} 接口 .
 
 ## 使用步骤
- 1, 导入下面的gradle 配置。
- 2，设置点击事件处理器 和 数据适配器.
```java
  mDragflowLayout.setOnItemClickListener(new DragFlowLayout.OnItemClickListener() {
            @Override
            public boolean performClick(DragFlowLayout dragFlowLayout, View child,
                                        MotionEvent event, int dragState) {
                //检查是否点击了关闭按钮(iv_close控件)。点击了就删除
                //ViewUtils.isViewUnderInScreen 判断点击事件是否是你需要的.
                //dragState 是拖拽状态。
                boolean performed = dragState != DragFlowLayout.DRAG_STATE_IDLE
                        && ViewUtils.isViewUnderInScreen(child.findViewById(R.id.iv_close),
                        (int) event.getRawX(),(int) event.getRawY());
                if(performed){
                    dragFlowLayout.removeView(child);
                }
                //点击事件
                return performed;
            }
        });

        mDragflowLayout.setDragAdapter(new DragAdapter<TestBean>() {
            @Override
            public int getItemLayoutId() {
                return R.layout.item_drag_flow;
            }
            @Override
            public void onBindData(View itemView, int dragState, TestBean data) {
                itemView.setTag(data);

                TextView tv = (TextView) itemView.findViewById(R.id.tv_text);
                tv.setText(data.text);
                //iv_close是关闭按钮。只有再非拖拽空闲的情况吓才显示
                itemView.findViewById(R.id.iv_close).setVisibility(
                        dragState!= DragFlowLayout.DRAG_STATE_IDLE
                        && data.draggable ? View.VISIBLE : View.INVISIBLE);
            }
            @NonNull
            @Override
            public TestBean getData(View itemView) {
                return (TestBean) itemView.getTag();
            }
        });
```
- 3, item管理： 对item的增删改查-,即CRUD.  通过api： mDragflowLayout.getDragItemManager()。
                即可得到DragItemManager.
- 4, 禁止个别Item拖拽。
```java
//数据实体实现IDraggable 接口，并且 isDraggable 为false即可
 private static class  TestBean implements IDraggable{
        String text;
        boolean draggable = true;
        public TestBean(String text) {
            this.text = text;
        }
        @Override
        public boolean isDraggable() {
            return draggable;
        }
    }
```
- 5, 更多详情请参见[demo](https://github.com/LightSun/android-drag-FlowLayout/blob/master/Drag-FlowLayout/app/src/main/java/com/heaven7/android/drag/demo/DragFlowLayoutTest.java). 

## Gradle Config
```java
compile 'com.heaven7.android.dragflowlayout:dragflowlayout:1.0.1'
```

   
## issue
   * if you have any question or good suggestion about this, please tell me... Thanks! 
   
## About me
   * heaven7 
   * email: donshine723@gmail.com or 978136772@qq.com   
   
## hope
i like technology. especially the open-source technology.And previous i didn't contribute to it caused by i am a little lazy, but now i really want to do some for the open-source. So i hope to share and communicate with the all of you.

## License

    Copyright 2016  
                    heaven7(donshine723@gmail.com)

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.


