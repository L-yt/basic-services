package cn.sylen.common.exception;

/**
 * 业务异常
 *
 * @author chenzhicong
 *
 */
public class BaseBusinessException extends RuntimeException {

	private static final long serialVersionUID = 1975191243408309088L;

	/**
	 * 默认的构造器
	 */
	public BaseBusinessException(){
		super();
	}

	/**
	 * 只带有异常信息的异常构造方法。
	 *
	 * @param message 异常信息
	 */
	public BaseBusinessException(String message){
		super(message);
	}

	/**
	 * 带有异常信息和原因异常堆栈的构造方法
	 *
	 * @param message 异常信息
	 * @param cause 原因异常
	 */
	public BaseBusinessException(String message, Throwable cause){
		super(message,cause);
	}

	/**
	 * 只带有原因异常堆栈的构造方法
	 *
	 * @param cause 原因异常
	 */
	public BaseBusinessException(Throwable cause){
		super(cause);
	}


}
