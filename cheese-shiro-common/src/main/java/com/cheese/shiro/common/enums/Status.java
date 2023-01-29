package com.cheese.shiro.common.enums;

import java.io.Serializable;

/**
 * 默认响应状态码及错误信息
 * @author sobann
 */
public enum Status implements Serializable{

	OK(200, "请求成功"),
	FAILED(-1, "请求失败"),
	Create(201, "操作成功"),
	BadReq(400,"请求参数错误"),
	CodeError(400,"验证码错误"),
	CodeExpired(400,"验证码已过期"),
	MailAddressError(400,"邮箱地址错误"),
	ErrorAccountOrPassword(400, "帐号或密码错误"),
	DisableAccount(400, "帐号未启用"),
	ErrorPassword(400, "密码错误"),
	TicketError(400,"凭证验证未通过"),
	Unauth(401, "用户未认证"),
	TokenExpired(401,"登录令牌过期"),
	IPChange(401,"登陆ip变换异常:"),
	MultiLogin(401,"帐号多处登录异常，登录ip:"),
	LostSession(401,"令牌获取非法"),
	RedirectUserCenter(401,"用户未认证"),
	Forbidden(403,"请求禁止"),
	NotFound(404, "未找到相应数据"),
	MethodNotAllowed(405,"操作权限不足"),
	NoAccessData(406, "数据权限不足"),
	Locked(423,"帐号已被锁定"),
	TooManyReuqest(421,"请求过多，请稍候再试"),
	Error(500,"服务内部错误"),
	MailError(501,"邮件发送错误"),
	UserSyncError(502,"用户未同步"),
	;

	private Integer code;
	private String msg;

	private Status(Integer code, String msg) {
		this.code = code;
		this.msg = msg;
	}

	public Integer getCode() {
		return code;
	}

	public void setCode(Integer code) {
		this.code = code;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}
}
