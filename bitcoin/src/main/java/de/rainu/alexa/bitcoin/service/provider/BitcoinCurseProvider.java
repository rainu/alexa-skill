package de.rainu.alexa.bitcoin.service.provider;

import de.rainu.alexa.bitcoin.model.BitcoinCurse;

public interface BitcoinCurseProvider {

  /**
   * Get the current bitcoin curse.
   *
   * @return
   */
  public BitcoinCurse getCurrentCurse();
}
