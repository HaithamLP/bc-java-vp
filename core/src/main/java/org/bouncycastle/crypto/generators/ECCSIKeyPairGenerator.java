package org.bouncycastle.crypto.generators;

import java.math.BigInteger;
import java.security.SecureRandom;

import org.bouncycastle.crypto.AsymmetricCipherKeyPair;
import org.bouncycastle.crypto.AsymmetricCipherKeyPairGenerator;
import org.bouncycastle.crypto.CryptoServicePurpose;
import org.bouncycastle.crypto.CryptoServicesRegistrar;
import org.bouncycastle.crypto.Digest;
import org.bouncycastle.crypto.KeyGenerationParameters;
import org.bouncycastle.crypto.constraints.DefaultServiceProperties;
import org.bouncycastle.crypto.params.ECCSIKeyGenerationParameters;
import org.bouncycastle.crypto.params.ECCSIPrivateKeyParameters;
import org.bouncycastle.crypto.params.ECCSIPublicKeyParameters;
import org.bouncycastle.math.ec.ECPoint;

public class ECCSIKeyPairGenerator
    implements AsymmetricCipherKeyPairGenerator
{
    private BigInteger q;
    private ECPoint G;
    private Digest digest;
    private ECCSIKeyGenerationParameters parameters;

    @Override
    public void init(KeyGenerationParameters parameters)
    {
        this.parameters = (ECCSIKeyGenerationParameters)parameters;
        this.q = this.parameters.getQ();
        this.G = this.parameters.getG();
        this.digest = this.parameters.getDigest();

        CryptoServicesRegistrar.checkConstraints(new DefaultServiceProperties("ECCSI", this.parameters.getN(), null, CryptoServicePurpose.KEYGEN));
    }

    @Override
    public AsymmetricCipherKeyPair generateKeyPair()
    {
        SecureRandom random = parameters.getRandom();
        this.digest.reset();
        byte[] id = parameters.getId();
        ECPoint kpak = parameters.getKPAK();
        // 1) Choose v, a random (ephemeral) non-zero element of F_q;
        BigInteger v = new BigInteger(256, random).mod(q);
        // 2) Compute PVT = [v]G
        ECPoint pvt = G.multiply(v).normalize();

        // 3) Compute a hash value HS = hash( G || KPAK || ID || PVT ), an N-octet integer;
        byte[] tmp = G.getEncoded(false);
        digest.update(tmp, 0, tmp.length);
        tmp = kpak.getEncoded(false);
        digest.update(tmp, 0, tmp.length);
        digest.update(id, 0, id.length);
        tmp = pvt.getEncoded(false);
        digest.update(tmp, 0, tmp.length);
        tmp = new byte[digest.getDigestSize()];
        digest.doFinal(tmp, 0);
        BigInteger HS = new BigInteger(1, tmp).mod(q);

        // 4) Compute SSK = ( KSAK + HS * v ) modulo q;
        BigInteger ssk = parameters.computeSSK(HS.multiply(v));
        ECCSIPublicKeyParameters pub = new ECCSIPublicKeyParameters(pvt);
        return new AsymmetricCipherKeyPair(new ECCSIPublicKeyParameters(pvt), new ECCSIPrivateKeyParameters(ssk, pub));
    }
}
