package com.spdpboss.model;

import jakarta.persistence.*;

@Entity
@Table(name = "game_results")
public class Result {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "game_name")
	private String gameName;

	@Column(name = "open_panel")
	private String openPanel;

	@Column(name = "close_panel")
	private String closePanel;

	@Column(name = "jodi")
	private String jodi;

	@Column(name = "open_ank")
	private String openAnk;

	@Column(name = "close_ank")
	private String closeAnk;

	@Column(name = "result_time") // Make sure this matches your DB column name
	private String resultTime;

	@Column(name = "serial_no")
	private Integer serialNo;

	@Column(name = "market_color")
	private String marketColor;

	@Column(name = "open_pre_time", columnDefinition = "integer default 15")
	private int openPreTime;

	@Column(name = "open_post_time", columnDefinition = "integer default 15")
	private int openPostTime;

	@Column(name = "close_pre_time", columnDefinition = "integer default 15")
	private int closePreTime;

	@Column(name = "close_post_time", columnDefinition = "integer default 15")
	private int closePostTime;

	@Column(name = "market_days")
	private String marketDays;

	@Column(name = "chart_date")
	private String chartDate;

	@Column(name = "days_of_week")
	private String daysOfWeek;

	@Column(name = "open_time") // Make sure this matches your DB column
	private String openTime;

	@Column(name = "close_time") // Make sure this matches your DB column
	private String closeTime;

	public String getOpenTime() {
		return openTime;
	}

	public void setOpenTime(String openTime) {
		this.openTime = openTime;
	}

	public String getCloseTime() {
		return closeTime;
	}

	public void setCloseTime(String closeTime) {
		this.closeTime = closeTime;
	}

	public String getMarketDays() {
		return marketDays;
	}

	public void setMarketDays(String marketDays) {
		this.marketDays = marketDays;
	}

	public String getChartDate() {
		return chartDate;
	}

	public void setChartDate(String chartDate) {
		this.chartDate = chartDate;
	}

	public String getDaysOfWeek() {
		return daysOfWeek;
	}

	public void setDaysOfWeek(String daysOfWeek) {
		this.daysOfWeek = daysOfWeek;
	}

	

	public int getOpenPreTime() {
		return openPreTime;
	}

	public void setOpenPreTime(int openPreTime) {
		this.openPreTime = openPreTime;
	}

	public int getOpenPostTime() {
		return openPostTime;
	}

	public void setOpenPostTime(int openPostTime) {
		this.openPostTime = openPostTime;
	}

	public int getClosePreTime() {
		return closePreTime;
	}

	public void setClosePreTime(int closePreTime) {
		this.closePreTime = closePreTime;
	}

	public int getClosePostTime() {
		return closePostTime;
	}

	public void setClosePostTime(int closePostTime) {
		this.closePostTime = closePostTime;
	}

	public String getMarketColor() {
		return marketColor;
	}

	public void setMarketColor(String marketColor) {
		this.marketColor = marketColor;
	}

	public Integer getSerialNo() {
		return serialNo;
	}

	public void setSerialNo(Integer serialNo) {
		this.serialNo = serialNo;
	}

	public String getResultTime() {
		return resultTime;
	}

	public void setResultTime(String resultTime) {
		this.resultTime = resultTime;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getGameName() {
		return gameName;
	}

	public void setGameName(String gameName) {
		this.gameName = gameName;
	}

	public String getOpenPanel() {
		return openPanel;
	}

	public void setOpenPanel(String openPanel) {
		this.openPanel = openPanel;
	}

	public String getClosePanel() {
		return closePanel;
	}

	public void setClosePanel(String closePanel) {
		this.closePanel = closePanel;
	}

	public String getJodi() {
		return jodi;
	}

	public void setJodi(String jodi) {
		this.jodi = jodi;
	}

	public String getOpenAnk() {
		return openAnk;
	}

	public void setOpenAnk(String openAnk) {
		this.openAnk = openAnk;
	}

	public String getCloseAnk() {
		return closeAnk;
	}

	public void setCloseAnk(String closeAnk) {
		this.closeAnk = closeAnk;
	}
}