
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import java.util.*

/**
 * 改动自:https://juejin.im/post/5dafc49b6fb9a04e17209922
 * 1.一条消息能被多个观察者消费
 * 2.延迟期期间,消息推送正常,延迟期结束,消息被认为是旧消息，不再能够收到旧消息的推送
 *
 */
open class ProtectedUnPeekLiveData<T> : LiveData<T>() {
    var DELAY_TO_CLEAR_EVENT = 1000
    var isAllowNullValue = false
    private var status = Status.IDLE
    private val mTimer = Timer()
    private var mTask: TimerTask? = null

    override fun observe(owner: LifecycleOwner, observer: Observer<in T?>) {
        super.observe(owner, { t: T? ->
            if (status == Status.START) {
                status = Status.DOING
                observer.onChanged(t)
            } else if (status == Status.DOING) {
                observer.onChanged(t)
            }
        })
    }

    override fun observeForever(observer: Observer<in T?>) {
        throw IllegalArgumentException("Do not use observeForever for communication between pages to avoid lifecycle security issues")
    }

    override fun setValue(value: T?) {
        if (status < Status.IDLE && !isAllowNullValue && value == null) {
            return
        }

        mTask?.cancel()
        mTimer.purge()

        status = Status.START
        super.setValue(value)
        status = Status.DELAYING

        mTask = object : TimerTask() {
            override fun run() {
                clear()
            }
        }
        if (value != null) {
            mTimer.schedule(mTask, DELAY_TO_CLEAR_EVENT.toLong())
        }
    }

    private fun clear() {
        // 避免即将结束时重新setValue,导致后续事件分发失败
        if (status == Status.DELAYING) {
            status = Status.IDLE
        }
    }

    internal enum class Status {
        START, DOING, DELAYING, IDLE
    }
}