package com.spdpboss.model;

import jakarta.persistence.*;

@Entity
@Table(name = "markets")
public class Market {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 1. Market Identity & Display Metadata
    @Column(name = "market_name", nullable = false)
    private String marketName;

    @Column(name = "market_slug", unique = true)
    private String marketSlug;

    @Column(name = "tagline")
    private String tagline = "FASTEST LIVE UPDATES";

    @Column(name = "sort_order")
    private Integer sortOrder = 0;

    @Column(name = "is_active")
    private boolean isActive = true;

    @Column(name = "hide_when_closed")
    private boolean hideWhenClosed = false;

    @Column(name = "is_featured_in_ticker")
    private boolean isFeaturedInTicker = false;

    // 2. Time Engine & Buffer Windows
    @Column(name = "open_time")
    private String openTime = "10:00";

    @Column(name = "close_time")
    private String closeTime = "12:00";

    @Column(name = "pre_open_cutoff_minutes")
    private int preOpenCutoffMinutes = 15;

    @Column(name = "pre_close_cutoff_minutes")
    private int preCloseCutoffMinutes = 15;

    @Column(name = "show_countdown_minutes")
    private int showCountdownMinutes = 30;

    @Column(name = "post_open_highlight_minutes")
    private int postOpenHighlightMinutes = 15;

    @Column(name = "post_close_display_minutes")
    private int postCloseDisplayMinutes = 30;

    // Operational Days
    @Column(name = "op_mon")
    private boolean mon = true;
    @Column(name = "op_tue")
    private boolean tue = true;
    @Column(name = "op_wed")
    private boolean wed = true;
    @Column(name = "op_thu")
    private boolean thu = true;
    @Column(name = "op_fri")
    private boolean fri = true;
    @Column(name = "op_sat")
    private boolean sat = true;
    @Column(name = "op_sun")
    private boolean sun = true;

    // 3. Dynamic Styling & Color Engine
    @Column(name = "bg_type")
    private String bgType = "SOLID"; // SOLID or GRADIENT

    @Column(name = "primary_bg_color")
    private String primaryBgColor = "#FFFFFF";

    @Column(name = "gradient_end_color")
    private String gradientEndColor = "#F7F4EC";

    @Column(name = "gradient_angle")
    private String gradientAngle = "180deg";

    @Column(name = "title_color")
    private String titleColor = "#1E293B";

    @Column(name = "number_color")
    private String numberColor = "#B91C1C";

    @Column(name = "subtitle_color")
    private String subtitleColor = "#64748B";

    @Column(name = "live_pulse_color")
    private String livePulseColor = "#B91C1C";

    @Column(name = "border_color")
    private String borderColor = "#CBD5E1";

    @Column(name = "border_width")
    private int borderWidth = 2;

    @Column(name = "box_shadow_glow")
    private String boxShadowGlow = "0 4px 12px rgba(0,0,0,0.08)";

    @Column(name = "header_stripe_color")
    private String headerStripeColor = "#B91C1C";

    // 4. Live Result Entry & Automated Math
    @Column(name = "open_pana")
    private String openPana = "***";

    @Column(name = "close_pana")
    private String closePana = "***";

    @Column(name = "open_ank")
    private String openAnk = "*";

    @Column(name = "close_ank")
    private String closeAnk = "*";

    @Column(name = "jodi")
    private String jodi = "**";

    @Column(name = "manual_ank_override")
    private boolean manualAnkOverride = false;

    @Column(name = "pending_placeholder")
    private String pendingPlaceholder = "***-**-***";

    @Column(name = "auto_publish_immediately")
    private boolean autoPublishImmediately = true;

    public Market() {}

    // Helper Math Methods
    public static String calculateAnkFromPana(String pana) {
        if (pana == null || pana.length() != 3 || !pana.matches("\\d{3}")) {
            return "*";
        }
        int sum = 0;
        for (char c : pana.toCharArray()) {
            sum += Character.getNumericValue(c);
        }
        return String.valueOf(sum % 10);
    }

    public void updateCalculatedResults() {
        if (!manualAnkOverride) {
            if (openPana != null && openPana.matches("\\d{3}")) {
                this.openAnk = calculateAnkFromPana(openPana);
            }
            if (closePana != null && closePana.matches("\\d{3}")) {
                this.closeAnk = calculateAnkFromPana(closePana);
            }
            if (!"*".equals(this.openAnk) && !"*".equals(this.closeAnk)) {
                this.jodi = this.openAnk + this.closeAnk;
            }
        }
    }

    public String getComputedStyle() {
        StringBuilder css = new StringBuilder();
        if ("GRADIENT".equalsIgnoreCase(bgType)) {
            css.append("background: linear-gradient(").append(gradientAngle).append(", ")
               .append(primaryBgColor).append(", ").append(gradientEndColor).append("); ");
        } else {
            css.append("background-color: ").append(primaryBgColor).append("; ");
        }
        css.append("border: ").append(borderWidth).append("px solid ").append(borderColor).append("; ");
        if (boxShadowGlow != null && !boxShadowGlow.isBlank()) {
            css.append("box-shadow: ").append(boxShadowGlow).append("; ");
        }
        return css.toString();
    }

    public String getFormattedResultString() {
        boolean hasOpen = openPana != null && !openPana.equals("***") && !openPana.equals("-") && !openPana.trim().isEmpty();
        boolean hasClose = closePana != null && !closePana.equals("***") && !closePana.equals("-") && !closePana.trim().isEmpty();
        
        if (hasOpen && hasClose) {
            return openPana + "-" + (jodi != null ? jodi : "**") + "-" + closePana;
        } else if (hasOpen) {
            return openPana + "-" + (openAnk != null ? openAnk : "*") + "*";
        } else if (hasClose) {
            return "***-*" + (closeAnk != null && !closeAnk.trim().isEmpty() ? closeAnk : "*") + "-" + closePana;
        } else {
            return pendingPlaceholder;
        }
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getMarketName() { return marketName; }
    public void setMarketName(String marketName) { this.marketName = marketName; }

    public String getMarketSlug() { return marketSlug; }
    public void setMarketSlug(String marketSlug) { this.marketSlug = marketSlug; }

    public String getTagline() { return tagline; }
    public void setTagline(String tagline) { this.tagline = tagline; }

    public Integer getSortOrder() { return sortOrder; }
    public void setSortOrder(Integer sortOrder) { this.sortOrder = sortOrder; }

    public boolean isActive() { return isActive; }
    public void setActive(boolean active) { isActive = active; }

    public boolean isHideWhenClosed() { return hideWhenClosed; }
    public void setHideWhenClosed(boolean hideWhenClosed) { this.hideWhenClosed = hideWhenClosed; }

    public boolean isFeaturedInTicker() { return isFeaturedInTicker; }
    public void setFeaturedInTicker(boolean featuredInTicker) { isFeaturedInTicker = featuredInTicker; }

    public String getOpenTime() { return openTime; }
    public void setOpenTime(String openTime) { this.openTime = openTime; }

    public String getCloseTime() { return closeTime; }
    public void setCloseTime(String closeTime) { this.closeTime = closeTime; }

    public int getPreOpenCutoffMinutes() { return preOpenCutoffMinutes; }
    public void setPreOpenCutoffMinutes(int preOpenCutoffMinutes) { this.preOpenCutoffMinutes = preOpenCutoffMinutes; }

    public int getPreCloseCutoffMinutes() { return preCloseCutoffMinutes; }
    public void setPreCloseCutoffMinutes(int preCloseCutoffMinutes) { this.preCloseCutoffMinutes = preCloseCutoffMinutes; }

    public int getShowCountdownMinutes() { return showCountdownMinutes; }
    public void setShowCountdownMinutes(int showCountdownMinutes) { this.showCountdownMinutes = showCountdownMinutes; }

    public int getPostOpenHighlightMinutes() { return postOpenHighlightMinutes; }
    public void setPostOpenHighlightMinutes(int postOpenHighlightMinutes) { this.postOpenHighlightMinutes = postOpenHighlightMinutes; }

    public int getPostCloseDisplayMinutes() { return postCloseDisplayMinutes; }
    public void setPostCloseDisplayMinutes(int postCloseDisplayMinutes) { this.postCloseDisplayMinutes = postCloseDisplayMinutes; }

    public boolean isMon() { return mon; }
    public void setMon(boolean mon) { this.mon = mon; }

    public boolean isTue() { return tue; }
    public void setTue(boolean tue) { this.tue = tue; }

    public boolean isWed() { return wed; }
    public void setWed(boolean wed) { this.wed = wed; }

    public boolean isThu() { return thu; }
    public void setThu(boolean thu) { this.thu = thu; }

    public boolean isFri() { return fri; }
    public void setFri(boolean fri) { this.fri = fri; }

    public boolean isSat() { return sat; }
    public void setSat(boolean sat) { this.sat = sat; }

    public boolean isSun() { return sun; }
    public void setSun(boolean sun) { this.sun = sun; }

    public String getBgType() { return bgType; }
    public void setBgType(String bgType) { this.bgType = bgType; }

    public String getPrimaryBgColor() { return primaryBgColor; }
    public void setPrimaryBgColor(String primaryBgColor) { this.primaryBgColor = primaryBgColor; }

    public String getGradientEndColor() { return gradientEndColor; }
    public void setGradientEndColor(String gradientEndColor) { this.gradientEndColor = gradientEndColor; }

    public String getGradientAngle() { return gradientAngle; }
    public void setGradientAngle(String gradientAngle) { this.gradientAngle = gradientAngle; }

    public String getTitleColor() { return titleColor; }
    public void setTitleColor(String titleColor) { this.titleColor = titleColor; }

    public String getNumberColor() { return numberColor; }
    public void setNumberColor(String numberColor) { this.numberColor = numberColor; }

    public String getSubtitleColor() { return subtitleColor; }
    public void setSubtitleColor(String subtitleColor) { this.subtitleColor = subtitleColor; }

    public String getLivePulseColor() { return livePulseColor; }
    public void setLivePulseColor(String livePulseColor) { this.livePulseColor = livePulseColor; }

    public String getBorderColor() { return borderColor; }
    public void setBorderColor(String borderColor) { this.borderColor = borderColor; }

    public int getBorderWidth() { return borderWidth; }
    public void setBorderWidth(int borderWidth) { this.borderWidth = borderWidth; }

    public String getBoxShadowGlow() { return boxShadowGlow; }
    public void setBoxShadowGlow(String boxShadowGlow) { this.boxShadowGlow = boxShadowGlow; }

    public String getHeaderStripeColor() { return headerStripeColor; }
    public void setHeaderStripeColor(String headerStripeColor) { this.headerStripeColor = headerStripeColor; }

    public String getOpenPana() { return openPana; }
    public void setOpenPana(String openPana) { this.openPana = openPana; }

    public String getClosePana() { return closePana; }
    public void setClosePana(String closePana) { this.closePana = closePana; }

    public String getOpenAnk() { return openAnk; }
    public void setOpenAnk(String openAnk) { this.openAnk = openAnk; }

    public String getCloseAnk() { return closeAnk; }
    public void setCloseAnk(String closeAnk) { this.closeAnk = closeAnk; }

    public String getJodi() { return jodi; }
    public void setJodi(String jodi) { this.jodi = jodi; }

    public boolean isManualAnkOverride() { return manualAnkOverride; }
    public void setManualAnkOverride(boolean manualAnkOverride) { this.manualAnkOverride = manualAnkOverride; }

    public String getPendingPlaceholder() { return pendingPlaceholder; }
    public void setPendingPlaceholder(String pendingPlaceholder) { this.pendingPlaceholder = pendingPlaceholder; }

    public boolean isAutoPublishImmediately() { return autoPublishImmediately; }
    public void setAutoPublishImmediately(boolean autoPublishImmediately) { this.autoPublishImmediately = autoPublishImmediately; }
}
