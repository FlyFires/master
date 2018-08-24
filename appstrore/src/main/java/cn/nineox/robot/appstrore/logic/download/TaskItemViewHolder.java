package cn.nineox.robot.appstrore.logic.download;

import com.liulishuo.filedownloader.model.FileDownloadStatus;

import cn.nineox.robot.appstrore.databinding.ItemAppBinding;
import cn.nineox.xframework.base.adapter.databinding.recyclerview.BaseBindingVH;

/**
 * Created by me on 18/1/2.
 */

public class TaskItemViewHolder extends BaseBindingVH<ItemAppBinding> {

    public TaskItemViewHolder(ItemAppBinding itemAppBinding) {
        super(itemAppBinding);
    }

    /**
     * viewHolder position
     */
    public int position;
    /**
     * download id
     */
    public int id;

    public void update(final int id, final int position) {
        this.id = id;
        this.position = position;
    }


    public void updateDownloaded() {
        mBinding.circleProgressBar.setMax(1);
        mBinding.circleProgressBar.setProgress(1);
//        taskPb.setMax(1);
//        taskPb.setProgress(1);
//
//        taskStatusTv.setText(R.string.tasks_manager_demo_status_completed);
//        taskActionBtn.setText(R.string.delete);
    }

    public void updateNotDownloaded(final int status, final long sofar, final long total) {
        if (sofar > 0 && total > 0) {
            final float percent = sofar
                    / (float) total;
            mBinding.circleProgressBar.setMax(100);
            mBinding.circleProgressBar.setProgress((int) (percent * 100));
        } else {
            mBinding.circleProgressBar.setMax(1);
            mBinding.circleProgressBar.setProgress(0);
        }

        switch (status) {
            case FileDownloadStatus.error:
                //taskStatusTv.setText(R.string.tasks_manager_demo_status_error);
                break;
            case FileDownloadStatus.paused:
                //taskStatusTv.setText(R.string.tasks_manager_demo_status_paused);
                break;
            default:
                //taskStatusTv.setText(R.string.tasks_manager_demo_status_not_downloaded);
                break;
        }
        //taskActionBtn.setText(R.string.start);
    }

    public void updateDownloading(final int status, final long sofar, final long total) {
        final float percent = sofar
                / (float) total;
        mBinding.circleProgressBar.setMax(100);
        mBinding.circleProgressBar.setProgress((int) (percent * 100));

        switch (status) {
            case FileDownloadStatus.pending:
                //taskStatusTv.setText(R.string.tasks_manager_demo_status_pending);
                break;
            case FileDownloadStatus.started:
                //taskStatusTv.setText(R.string.tasks_manager_demo_status_started);
                break;
            case FileDownloadStatus.connected:
                //taskStatusTv.setText(R.string.tasks_manager_demo_status_connected);
                break;
            case FileDownloadStatus.progress:
                //taskStatusTv.setText(R.string.tasks_manager_demo_status_progress);
                break;
            default:
//                taskStatusTv.setText(DemoApplication.CONTEXT.getString(
//                        R.string.tasks_manager_demo_status_downloading, status));
                break;
        }

        //taskActionBtn.setText(R.string.pause);
    }
}