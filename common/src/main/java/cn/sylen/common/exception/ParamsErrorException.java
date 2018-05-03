package cn.sylen.common.exception;

/**
 * 一个用于框架的异常基类，框架中的异常都需要使用或者继承这个类
 * 同时这个类是runtime类型的所以编译期不做检查，会一直向外抛出，
 * 直到最前端。
 *
 * @author Bimu
 *
 */
public class ParamsErrorException extends RuntimeException {

	private static final long serialVersionUID = 1975191243408309088L;

	/**
	 * 默认的构造器
	 */
	public ParamsErrorException(){
		super();
	}

	/**
	 * 只带有异常信息的异常构造方法。
	 *
	 * @param message 异常信息
	 */
	public ParamsErrorException(String message){
		super(message);
	}

	/**
	 * 带有异常信息和原因异常堆栈的构造方法
	 *
	 * @param message 异常信息
	 * @param cause 原因异常
	 */
	public ParamsErrorException(String message, Throwable cause){
		super(message,cause);
	}

	/**
	 * 只带有原因异常堆栈的构造方法
	 *
	 * @param cause 原因异常
	 */
	public ParamsErrorException(Throwable cause){
		super(cause);
	}


}
