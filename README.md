# android-drag-FowLayout
this is a draggable flow layout lib (android 可拖拽的流布局库) .
[Sample apk/示例app](https://github.com/LightSun/android-drag-FlowLayout/tree/master/apk)

 <img src="/art/drag_flowlayout.gif" alt="Demo Screen Capture" width="296px" height="581px" />
 

## 特点
 - 1, 类似可拖拽的GridView. 不过gridView 宽度/个数是固定的。 这个布局item宽度是不定的（放不下自动换行）。
 - 2，长按item拖拽，如果要处理点击事件请调用。
```java
        
        mDragflowLayout.setOnItemClickListener(new ClickToDeleteItemListenerImpl(R.id.iv_close){
        
             //点击删除成功时回调
            @Override
            protected void onDeleteSuccess(DragFlowLayout dfl, View child, Object data) {
               //your code
            }
        });
```
 - 3，可嵌套ScrollerView.  demo就是。
 - 4, 默认均可拖拽，如果想禁止某些Item拖拽请实现 {@link IDraggable} 接口 .
 - 5, 支持预存储一定个数的item view. 以避免频繁创建. 
```java
 //预存指定个数的Item. 这些Item会反复使用
 mDragflowLayout.prepareItemsByCount(10);
```
- 6, 1.5.0 新增 拖拽状态监听器 和 view观察者。
```java
        //设置拖拽状态监听器
        mDragflowLayout.setOnDragStateChangeListener(new DragFlowLayout.OnDragStateChangeListener() {
            @Override
            public void onDragStateChange(DragFlowLayout dfl, int dragState) {
                System.out.println("on drag state change : dragState = " + dragState);
            }
        });
        //添加view观察者
        mDragflowLayout.addViewObserver(new IViewObserver() {
            @Override
            public void onAddView(View child, int index) {
                Logger.i(TAG, "onAddView", "index = " + index);
            }
            @Override
            public void onRemoveView(View child, int index) {
                Logger.i(TAG, "onRemoveView", "index = " + index);
            }
        });
```
 
## 使用步骤
- 1, 导入下面的gradle 配置。并在xml中添加配置
```java
//root gradle
allprojects {
    repositories {
        maven { url "https://jitpack.io" }
    }
}
// if you use androidx . please use '1.8.8-x'
implementation 'com.github.LightSun:android-drag-FlowLayout:1.8.8'
``` 
```java
 <com.heaven7.android.dragflowlayout.DragFlowLayout
                android:id="@+id/drag_flowLayout"
                android:layout_width="match_parent"
                android:layout_height="wrap_content">
    </com.heaven7.android.dragflowlayout.DragFlowLayout>
```
- 2，设置点击事件处理器 和 数据适配器.
```java
        //用这个处理点击事件
        mDragflowLayout.setOnItemClickListener(new ClickToDeleteItemListenerImpl(R.id.iv_close){
            @Override
            protected void onDeleteSuccess(DragFlowLayout dfl, View child, Object data) {
               //your code
            }
        });
        
        //DragAdapter 泛型参数就是为了每个Item绑定一个对应的数据。通常很可能是json转化过来的bean对象
        mDragflowLayout.setDragAdapter(new DragAdapter<TestBean>() {
        
            @Override  //获取你的item布局Id
            public int getItemLayoutId() {
                return R.layout.item_drag_flow;
            }
            //绑定对应item的数据
            @Override
            public void onBindData(View itemView, int dragState, TestBean data) {
                itemView.setTag(data);

                TextView tv = (TextView) itemView.findViewById(R.id.tv_text);
                tv.setText(data.text);
                //iv_close是关闭按钮。只有再非拖拽空闲的情况下才显示
                itemView.findViewById(R.id.iv_close).setVisibility(
                        dragState!= DragFlowLayout.DRAG_STATE_IDLE
                        && data.draggable ? View.VISIBLE : View.INVISIBLE);
            }
            //根据指定的child获取对应的数据。
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
//数据实体实现IDraggable (是否可拖拽) 接口，并且 isDraggable 为false即可
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
- 5, 如果要使用布局动画，请用ViewGroup.setLayoutTransaction(...) .
- 6, 更多详情请参见[demo](https://github.com/LightSun/android-drag-FlowLayout/blob/master/Drag-FlowLayout/app/src/main/java/com/heaven7/android/drag/demo/DragFlowLayoutTest.java). 

## Gradle Config
```java
repositories {
        jcenter()
}

compile 'com.heaven7.android.dragflowlayout:dragflowlayout:1.8.8'
```

## API说明
```java
  //设置拖拽状态监听器
    public void setOnDragStateChangeListener(OnDragStateChangeListener l)
 //获取拖拽状态
    public @DragState int getDragState()
 //设置Item点击事件处理器
    public void setOnItemClickListener(OnItemClickListener l)
 //设置数据适配器
    void setDragAdapter(DragAdapter<T> adapter)
 //获取Item管理器(方便CRUD-增删改查 item)
    public DragItemManager getDragItemManager()
 //设置全局是否可拖拽。如果false,则无法长按拖拽了。
    public void setDraggable(boolean draggable)
 //设置缓存view的个数。可避免重复创建item view
    public void prepareItemsByCount(int count)
    
  //标记拖拽开始，这个会使得拖拽状态变更为draggable. .
     public void beginDrag();
 //标记拖拽结束, 内部会自动将拖拽状态改为 DRAG_STATE_IDLE .
    public void finishDrag();
```

##  重要版本更新日志
- 1, version(1.5.0)
   * （1) , 增加拖拽状态监听器 和 child view观察者
- 2, version(1.5.1)
   * （1) , reuse item view for DragItemManager(inner class)
- 3, version(1.5.5)
   * （1) , fix reuse item view bug.
   *  (2) , add method onDeleteSuccess(...) for ClickToDeleteItemListenerImpl
```java
  mDragflowLayout.setOnItemClickListener(new ClickToDeleteItemListenerImpl(R.id.iv_close){
            @Override
            protected void onDeleteSuccess(DragFlowLayout dfl, View child, Object data) {
               //your code
            }
        });
```
- 4, version(1.6.2)
   *  fix bug of issue(#1) 
- 5, version(1.8.3)
   * fix a bug of multi fask click with touch scrol (解决多次点击+滑动的问题).
- 6,version (1.8.8)
   * 为部分伙伴的新需求，开启编辑模式 添加新方法 beginDrag().


##  一些思想
- 1 ，最开始我打算用DragHelper做的。但是发现不能将拖拽的child 渲染在最上面。
- 2, RecyclerView的自定义LayoutManager + onItem touch / 应该也可以.


   
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


