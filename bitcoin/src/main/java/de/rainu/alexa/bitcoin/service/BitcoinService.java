package de.rainu.alexa.bitcoin.service;

import de.rainu.alexa.bitcoin.model.BitcoinCurse;
import de.rainu.alexa.bitcoin.service.provider.BitcoinCurseProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BitcoinService {

  @Autowired
  BitcoinCurseProvider curseProvider;

  public BitcoinCurse getCurrentCurse(){
    return curseProvider.getCurrentCurse();
  }
}
