package com.bluepower.command;

import com.bluepower.utility.MD5Utility;

public class UDPCommand {

	public String		vs;
	public String		fr;  // any 0, server 1, mobile 2, socket 3, remote control 4, camera 5
	public int			ft;
	public String		fv;
	
	public String		to;  // any 0, server 1, mobile 2, socket 3, remote control 4, camera 5
	public int			tt;
	public String		tv;	
	
	public int			ct;  //1 direct, 2 forward, 3 passthrough
	
	public int			rt; //0 request, 1 request-need-response, 2 response, 3 message
	
	public String		sq;
	public String		md5;
	public int			cmd;
	
	public Object cn;
	
	public  UDPCommand() {
		
	}
	
	public UDPCommand(String vs, String fr, int ft, String fv, String to, int tt, String tv, int ct,
			int rt, String sq, String md5, int cmd, Object cn) {
		this.vs = vs;
		this.fr = fr;
		this.ft = ft;
		this.fv = fv;
		this.to = to;
		this.tt = tt;
		this.tv = tv;
		this.ct = ct;
		this.rt = rt;
		this.sq = sq;
		this.md5 = MD5Utility.getMd5(fr + to + sq + cmd);
		this.cmd = cmd;
		this.cn = cn;
	}
	
	public String	getVs() {
		return vs;
	}
	
	public void	setVs(String vs) {
		this.vs = vs;
	}
	
	
	public String getFr() {
		return fr;
	}
	
	void	setFr(String fr) {
		this.fr = fr;
	}
	
	public int getFt() {
		return ft;
	}
	
	void	setFt(int ft) {
		this.ft = ft;
	}
	

	public String getFv() {
		return fv;
	}
	
	void	setFv(String fv) {
		this.fv = fv;
	}

	public String getTo() {
		return to;
	}
	
	void	setTo(String to) {
		this.to = to;
	}
	
	public int getTt() {
		return tt;
	}
	
	void	setTt(int tt) {
		this.tt = tt;
	}
	

	public String getTv() {
		return tv;
	}
	
	void	setTv(String tv) {
		this.tv = tv;
	}
	
	public int getCt() {
		return ct;
	}
	
	void	setCt(int ct) {
		this.ct = ct;
	}
	
	public int getRt() {
		return rt;
	}
	
	void	setRt(int rt) {
		this.rt = rt;
	}


	public String getSq() {
		return sq;
	}
	
	void	setSq(String sq) {
		this.sq = sq;
	}
	
	public String getMd5() {
		return md5;
	}
	
	void	setMd5(String md5) {
		this.md5 = md5;
	}	

	public int getCmd() {
		return cmd;
	}
	
	void	setCmd(int cmd) {
		this.cmd = cmd;
	}

	
	public Object getCn() {
		return cn;		
	}
	
	public void setCn(Object cn) {
		this.cn = cn;
	}
	
}
