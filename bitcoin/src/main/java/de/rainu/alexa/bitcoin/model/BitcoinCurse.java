package de.rainu.alexa.bitcoin.model;

import org.joda.time.DateTime;

public class BitcoinCurse {
  private DateTime date;
  private Double coin;
  private Double euro;

  public BitcoinCurse(DateTime date, Double coin, Double euro) {
    this.date = date;
    this.coin = coin;
    this.euro = euro;
  }

  public DateTime getDate() {
    return date;
  }

  public Double getCoin() {
    return coin;
  }

  public Double getEuro() {
    return euro;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof BitcoinCurse)) {
      return false;
    }

    BitcoinCurse that = (BitcoinCurse) o;

    if (date != null ? !date.equals(that.date) : that.date != null) {
      return false;
    }
    if (coin != null ? !coin.equals(that.coin) : that.coin != null) {
      return false;
    }
    return euro != null ? euro.equals(that.euro) : that.euro == null;
  }

  @Override
  public int hashCode() {
    int result = date != null ? date.hashCode() : 0;
    result = 31 * result + (coin != null ? coin.hashCode() : 0);
    result = 31 * result + (euro != null ? euro.hashCode() : 0);
    return result;
  }

  @Override
  public String toString() {
    return "@" + date + ": " +coin + "bc <=> " + euro + "€";
  }
}
