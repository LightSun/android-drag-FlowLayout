package com.heaven7.android.transition;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.Keyframe;
import android.animation.LayoutTransition;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.view.View;

/**
 * Created by heaven7 on 2016/8/29.
 */
public class TransitionProvider {

    public static LayoutTransition createTransition(Object target){
        LayoutTransition mLayoutTransition = new LayoutTransition();
        mLayoutTransition.setStagger(LayoutTransition.CHANGE_APPEARING, 30);
        mLayoutTransition.setStagger(LayoutTransition.CHANGE_DISAPPEARING, 30);
        //设置每个动画持续的时间
        mLayoutTransition.setDuration(300);

        /**
         * Add Button
         * LayoutTransition.APPEARING
         * 增加一个Button时，设置该Button的动画效果
         */
        ObjectAnimator mAnimatorAppearing = ObjectAnimator.ofFloat(null, "rotationY", 90.0f,0.0f)
                .setDuration(mLayoutTransition.getDuration(LayoutTransition.APPEARING));
        //为LayoutTransition设置动画及动画类型
        mLayoutTransition.setAnimator(LayoutTransition.APPEARING, mAnimatorAppearing);
        mAnimatorAppearing.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                // TODO Auto-generated method stub
                super.onAnimationEnd(animation);
                View view = (View) ((ObjectAnimator) animation).getTarget();
                view.setRotationY(0.0f);
            }
        });

        /**
         * Add Button
         * LayoutTransition.CHANGE_APPEARING
         * 当增加一个Button时，设置其他Button的动画效果
         */

        PropertyValuesHolder pvhLeft =
                PropertyValuesHolder.ofInt("left", 0, 1);
        PropertyValuesHolder pvhTop =
                PropertyValuesHolder.ofInt("top", 0, 1);
        PropertyValuesHolder pvhRight =
                PropertyValuesHolder.ofInt("right", 0, 1);
        PropertyValuesHolder pvhBottom =
                PropertyValuesHolder.ofInt("bottom", 0, 1);

        PropertyValuesHolder mHolderScaleX = PropertyValuesHolder.ofFloat("scaleX", 1.0f,0.0f,1.0f);
        PropertyValuesHolder mHolderScaleY = PropertyValuesHolder.ofFloat("scaleY", 1.0f,0.0f,1.0f);
        ObjectAnimator mObjectAnimatorChangeAppearing = ObjectAnimator.ofPropertyValuesHolder(target, pvhLeft,
                pvhTop,pvhRight,pvhBottom,mHolderScaleX,mHolderScaleY).setDuration(mLayoutTransition
                .getDuration(LayoutTransition.CHANGE_APPEARING));
        mLayoutTransition.setAnimator(LayoutTransition.CHANGE_APPEARING, mObjectAnimatorChangeAppearing);
        mObjectAnimatorChangeAppearing.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                // TODO Auto-generated method stub
                super.onAnimationEnd(animation);
                View view = (View) ((ObjectAnimator) animation).getTarget();
                view.setScaleX(1f);
                view.setScaleY(1f);
            }
        });

        /**
         * Delete Button
         * LayoutTransition.DISAPPEARING
         * 当删除一个Button时，设置该Button的动画效果
         */
        ObjectAnimator mObjectAnimatorDisAppearing = ObjectAnimator.ofFloat(null, "rotationX", 0.0f,90.0f)
                .setDuration(mLayoutTransition.getDuration(LayoutTransition.DISAPPEARING));
        mLayoutTransition.setAnimator(LayoutTransition.DISAPPEARING, mObjectAnimatorDisAppearing);
        mObjectAnimatorDisAppearing.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                // TODO Auto-generated method stub
                super.onAnimationEnd(animation);
                View view = (View) ((ObjectAnimator) animation).getTarget();
                view.setRotationX(0.0f);
            }
        });

        /**
         * Delete Button
         * LayoutTransition.CHANGE_DISAPPEARING
         * 当删除一个Button时，设置其它Button的动画效果
         */
        //Keyframe 对象中包含了一个时间/属性值的键值对，用于定义某个时刻的动画状态。
        Keyframe mKeyframeStart = Keyframe.ofFloat(0.0f, 0.0f);
        Keyframe mKeyframeMiddle = Keyframe.ofFloat(0.5f, 180.0f);
        Keyframe mKeyframeEndBefore = Keyframe.ofFloat(0.999f, 360.0f);
        Keyframe mKeyframeEnd = Keyframe.ofFloat(1.0f, 0.0f);

        PropertyValuesHolder mPropertyValuesHolder = PropertyValuesHolder.ofKeyframe("rotation",
                mKeyframeStart,mKeyframeMiddle,mKeyframeEndBefore,mKeyframeEnd);
        ObjectAnimator mObjectAnimatorChangeDisAppearing = ObjectAnimator.ofPropertyValuesHolder(target,
                pvhLeft,pvhTop,pvhRight,pvhBottom,mPropertyValuesHolder)
                .setDuration(mLayoutTransition.getDuration(LayoutTransition.CHANGE_DISAPPEARING));
        mLayoutTransition.setAnimator(LayoutTransition.CHANGE_DISAPPEARING, mObjectAnimatorChangeDisAppearing);
        mObjectAnimatorChangeDisAppearing.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                // TODO Auto-generated method stub
                super.onAnimationEnd(animation);
                View view = (View) ((ObjectAnimator) animation).getTarget();
                view.setRotation(0.0f);
            }
        });
        return mLayoutTransition ;
    }
}
