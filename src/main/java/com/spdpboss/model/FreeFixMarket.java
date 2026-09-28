package com.spdpboss.model;

import jakarta.persistence.*;

@Entity
@Table(name = "free_fix_markets")
public class FreeFixMarket {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String marketName;

    @Column(length = 500)
    private String ank;

    @Column(length = 1000)
    private String openPanna;

    @Column(length = 1000)
    private String jodi;

    @Column(length = 500)
    private String extraAnk;

    public FreeFixMarket() {}

    public FreeFixMarket(String marketName, String ank, String openPanna, String jodi, String extraAnk) {
        this.marketName = marketName;
        this.ank = ank;
        this.openPanna = openPanna;
        this.jodi = jodi;
        this.extraAnk = extraAnk;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getMarketName() {
        return marketName;
    }

    public void setMarketName(String marketName) {
        this.marketName = marketName;
    }

    public String getAnk() {
        return ank;
    }

    public void setAnk(String ank) {
        this.ank = ank;
    }

    public String getOpenPanna() {
        return openPanna;
    }

    public void setOpenPanna(String openPanna) {
        this.openPanna = openPanna;
    }

    public String getJodi() {
        return jodi;
    }

    public void setJodi(String jodi) {
        this.jodi = jodi;
    }

    public String getExtraAnk() {
        return extraAnk;
    }

    public void setExtraAnk(String extraAnk) {
        this.extraAnk = extraAnk;
    }
}
