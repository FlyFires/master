package cn.nineox.robot.appstrore.logic;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.view.ViewGroup;

import com.liulishuo.filedownloader.BaseDownloadTask;
import com.liulishuo.filedownloader.FileDownloadListener;
import com.liulishuo.filedownloader.FileDownloadSampleListener;
import com.liulishuo.filedownloader.model.FileDownloadStatus;
import com.liulishuo.filedownloader.util.FileDownloadUtils;

import java.io.File;
import java.util.List;

import cn.nineox.robot.appstrore.databinding.ItemAppBinding;
import cn.nineox.robot.appstrore.logic.bean.AppBean;
import cn.nineox.robot.appstrore.logic.download.TaskItemViewHolder;
import cn.nineox.robot.appstrore.logic.download.TasksManager;
import cn.nineox.robot.appstrore.logic.download.TasksManagerModel;
import cn.nineox.xframework.base.adapter.databinding.recyclerview.BaseBindingAdapter;
import cn.nineox.xframework.base.adapter.databinding.recyclerview.BaseBindingVH;

/**
 * Created by me on 18/1/2.
 */

public class TaskAdapter extends BaseBindingAdapter<AppBean, ItemAppBinding> {
    public TaskAdapter(Context mContext, List mDatas, int mLayoutId) {
        super(mContext, mDatas, mLayoutId);
    }

    public TaskAdapter(Context mContext, List mDatas) {
        super(mContext, mDatas);
    }


    @Override
    public BaseBindingVH<ItemAppBinding> onCreateViewHolder(ViewGroup parent, int viewType) {
        TaskItemViewHolder holder = new TaskItemViewHolder((ItemAppBinding) DataBindingUtil.inflate(mInfalter, mLayoutId, parent, false));

        //return super.onCreateViewHolder(parent, viewType);

        return holder;
    }


    @Override
    public void onBindViewHolder(BaseBindingVH<ItemAppBinding> viewholder, int position) {
        super.onBindViewHolder(viewholder, position);


        TaskItemViewHolder holder = (TaskItemViewHolder)viewholder;
        final TasksManagerModel model = TasksManager.getImpl().get(position);
        if(model != null){
            holder.update(model.getId(), position);
            //holder.taskActionBtn.setTag(holder);
            //holder.taskNameTv.setText(model.getName());
            TasksManager.getImpl()
                    .updateViewHolder(holder.id, holder);

            //holder.taskActionBtn.setEnabled(true);


            if (TasksManager.getImpl().isReady()) {
                final int status = TasksManager.getImpl().getStatus(model.getId(), model.getPath());
                if (status == FileDownloadStatus.pending || status == FileDownloadStatus.started ||
                        status == FileDownloadStatus.connected) {
                    // start task, but file not created yet
                    holder.updateDownloading(status, TasksManager.getImpl().getSoFar(model.getId())
                            , TasksManager.getImpl().getTotal(model.getId()));
                } else if (!new File(model.getPath()).exists() &&
                        !new File(FileDownloadUtils.getTempPath(model.getPath())).exists()) {
                    // not exist file
                    holder.updateNotDownloaded(status, 0, 0);
                } else if (TasksManager.getImpl().isDownloaded(status)) {
                    // already downloaded and exist
                    holder.updateDownloaded();
                } else if (status == FileDownloadStatus.progress) {
                    // downloading
                    holder.updateDownloading(status, TasksManager.getImpl().getSoFar(model.getId())
                            , TasksManager.getImpl().getTotal(model.getId()));
                } else {
                    // not start
                    holder.updateNotDownloaded(status, TasksManager.getImpl().getSoFar(model.getId())
                            , TasksManager.getImpl().getTotal(model.getId()));
                }
            } else {
                //holder.taskStatusTv.setText(R.string.tasks_manager_demo_status_loading);
                //holder.taskActionBtn.setEnabled(false);
            }
        }

    }

    private FileDownloadListener taskDownloadListener = new FileDownloadSampleListener() {

        private TaskItemViewHolder checkCurrentHolder(final BaseDownloadTask task) {
            final TaskItemViewHolder tag = (TaskItemViewHolder) task.getTag();
            if (tag.id != task.getId()) {
                return null;
            }

            return tag;
        }

        @Override
        protected void pending(BaseDownloadTask task, int soFarBytes, int totalBytes) {
            super.pending(task, soFarBytes, totalBytes);
            final TaskItemViewHolder tag = checkCurrentHolder(task);
            if (tag == null) {
                return;
            }

            tag.updateDownloading(FileDownloadStatus.pending, soFarBytes
                    , totalBytes);
            //tag.taskStatusTv.setText(R.string.tasks_manager_demo_status_pending);
        }

        @Override
        protected void started(BaseDownloadTask task) {
            super.started(task);
            final TaskItemViewHolder tag = checkCurrentHolder(task);
            if (tag == null) {
                return;
            }

            //tag.taskStatusTv.setText(R.string.tasks_manager_demo_status_started);
        }

        @Override
        protected void connected(BaseDownloadTask task, String etag, boolean isContinue, int soFarBytes, int totalBytes) {
            super.connected(task, etag, isContinue, soFarBytes, totalBytes);
            final TaskItemViewHolder tag = checkCurrentHolder(task);
            if (tag == null) {
                return;
            }

            tag.updateDownloading(FileDownloadStatus.connected, soFarBytes
                    , totalBytes);
            //tag.taskStatusTv.setText(R.string.tasks_manager_demo_status_connected);
        }

        @Override
        protected void progress(BaseDownloadTask task, int soFarBytes, int totalBytes) {
            super.progress(task, soFarBytes, totalBytes);
            final TaskItemViewHolder tag = checkCurrentHolder(task);
            if (tag == null) {
                return;
            }

            tag.updateDownloading(FileDownloadStatus.progress, soFarBytes
                    , totalBytes);
        }

        @Override
        protected void error(BaseDownloadTask task, Throwable e) {
            super.error(task, e);
            final TaskItemViewHolder tag = checkCurrentHolder(task);
            if (tag == null) {
                return;
            }

            tag.updateNotDownloaded(FileDownloadStatus.error, task.getLargeFileSoFarBytes()
                    , task.getLargeFileTotalBytes());
            TasksManager.getImpl().removeTaskForViewHolder(task.getId());
        }

        @Override
        protected void paused(BaseDownloadTask task, int soFarBytes, int totalBytes) {
            super.paused(task, soFarBytes, totalBytes);
            final TaskItemViewHolder tag = checkCurrentHolder(task);
            if (tag == null) {
                return;
            }

            tag.updateNotDownloaded(FileDownloadStatus.paused, soFarBytes, totalBytes);
            //tag.taskStatusTv.setText(R.string.tasks_manager_demo_status_paused);
            TasksManager.getImpl().removeTaskForViewHolder(task.getId());
        }

        @Override
        protected void completed(BaseDownloadTask task) {
            super.completed(task);
            final TaskItemViewHolder tag = checkCurrentHolder(task);
            if (tag == null) {
                return;
            }

            tag.updateDownloaded();
            TasksManager.getImpl().removeTaskForViewHolder(task.getId());
        }
    };
}
