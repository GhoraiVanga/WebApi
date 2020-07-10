package me.zhulin.shopapi.exception;


import me.zhulin.shopapi.enums.ResultEnum;

/**
 * Created By Zhu Lin on 3/10/2018.
 */
public class MyException extends RuntimeException {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1363380730761043681L;
	private Integer code;

    public MyException(ResultEnum resultEnum) {
        super(resultEnum.getMessage());

        this.code = resultEnum.getCode();
    }

    public MyException(Integer code, String message) {
        super(message);
        this.code = code;
    }
}
