package com.abhishek.tenanthub.auth.service.impl;

import cn.hutool.crypto.asymmetric.KeyType;
import cn.hutool.crypto.asymmetric.RSA;
import com.abhishek.tenanthub.auth.jwt.AuthKeyMaterial;
import com.abhishek.tenanthub.auth.service.JwtTokenService;
import com.abhishek.tenanthub.auth.service.RsaAsymmetricCryptoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;


/**
 * <p>
 *
 * </p>
 *
 * @author WangChenChen
 * @version 1.0.0
 * @date 2024/6/18 PM 2:18
 */


@Slf4j
@Service
public class RsaAsymmetricCryptoServiceImpl implements RsaAsymmetricCryptoService {

    private final RSA _rsa;

    public RsaAsymmetricCryptoServiceImpl(ApplicationContext context, JwtTokenService tokenService) {
        _rsa = AuthKeyMaterial.rsa(context, tokenService.getProps());
    }

    @Override
    public RSA getRSA() {
        return this._rsa;
    }

    @Override
    public String encrypt(String plainText) {
        return _rsa.encryptBase64(plainText, KeyType.PublicKey);
    }


    @Override
    public String decrypt(String cipherText) {
        return _rsa.decryptStr(cipherText, KeyType.PrivateKey);
    }


}
