package com.rong.lcdbusview.animations;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import java.util.ArrayList;


public class RouteItemAnimator extends DefaultItemAnimator {


    private ArrayList<RecyclerView.ViewHolder> mPendingAddHolders = new ArrayList<RecyclerView.ViewHolder>();
    private ArrayList<RecyclerView.ViewHolder> mPendingRemoveHolders = new ArrayList<>();
    private ArrayList<RecyclerView.ViewHolder> mAddAnimtions = new ArrayList<>();
    private ArrayList<RecyclerView.ViewHolder> mRemoveAnimations = new ArrayList<>();

    @Override
    public boolean isRunning() {
        return !(mPendingAddHolders.isEmpty()
                && mPendingRemoveHolders.isEmpty()
                && mAddAnimtions.isEmpty()
                && mRemoveAnimations.isEmpty());
    }

    @Override
    public boolean animateAdd(RecyclerView.ViewHolder holder) {
        holder.itemView.setAlpha(0.f);
        mPendingAddHolders.add(holder);
        return true;
    }

    @Override
    public boolean animateRemove(RecyclerView.ViewHolder holder) {
        mPendingRemoveHolders.add(holder);
        return true;
    }

    @Override
    public void runPendingAnimations() {
        boolean isRemove = !mPendingRemoveHolders.isEmpty();
        boolean isAdd = !mPendingAddHolders.isEmpty();

        if(!isRemove && !isAdd) return;

        // first remove
        if(isRemove) {
            for(RecyclerView.ViewHolder holder : mPendingRemoveHolders) {
                animateRemoveImpl(holder);
            }
            mPendingRemoveHolders.clear();
        }

        // last add
        if(isAdd) {
            ArrayList<RecyclerView.ViewHolder> holders = new ArrayList<>();
            holders.addAll(mPendingAddHolders);
            mPendingAddHolders.clear();
            for(RecyclerView.ViewHolder holder : holders) {
                animateAddImpl(holder);
            }
            holders.clear();
        }
    }

    // 执行添加动画
    private void animateAddImpl(final RecyclerView.ViewHolder holder) {
        mAddAnimtions.add(holder);
        final View item = holder.itemView;
        ObjectAnimator animator = ObjectAnimator.ofFloat(item, "alpha", 0.f, 1.f);
        animator.setDuration(1000);
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                dispatchAddStarting(holder);
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                item.setAlpha(1.f);
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                dispatchAddFinished(holder);
                mAddAnimtions.remove(holder);
                if (!isRunning()) dispatchAnimationsFinished();
            }
        });
        animator.start();
    }

    // 执行移出动画
    private void animateRemoveImpl(final RecyclerView.ViewHolder holder) {
        mRemoveAnimations.add(holder);
        final View item = holder.itemView;
        ObjectAnimator animator = ObjectAnimator.ofFloat(item, "alpha", 1.f, 0.f);
        animator.setDuration(1000);
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                dispatchRemoveStarting(holder);
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                mRemoveAnimations.remove(holder);
                item.setAlpha(1.f);
                dispatchRemoveFinished(holder);
                if (!isRunning()) dispatchAnimationsFinished();
            }
        });
        animator.start();
    }



}
