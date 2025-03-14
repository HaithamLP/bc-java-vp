package org.bouncycastle.jcajce.provider.asymmetric.util;

import org.bouncycastle.crypto.BlockCipher;
import org.bouncycastle.crypto.BufferedBlockCipher;
import org.bouncycastle.jce.spec.IESParameterSpec;

public class IESUtil
{
    public static IESParameterSpec guessParameterSpec(BufferedBlockCipher iesBlockCipher, byte[] nonce)
    {
        if (iesBlockCipher == null)
        {
            return new IESParameterSpec(null, null, 128);
        }
        else
        {
            BlockCipher underlyingCipher = iesBlockCipher.getUnderlyingCipher();

            if (underlyingCipher.getAlgorithmNameBlock().equals("DES") ||
                underlyingCipher.getAlgorithmNameBlock().equals("RC2") ||
                underlyingCipher.getAlgorithmNameBlock().equals("RC5-32") ||
                underlyingCipher.getAlgorithmNameBlock().equals("RC5-64"))
            {
                return new IESParameterSpec(null, null, 64, 64, nonce);
            }
            else if (underlyingCipher.getAlgorithmNameBlock().equals("SKIPJACK"))
            {
                return new IESParameterSpec(null, null, 80, 80, nonce);
            }
            else if (underlyingCipher.getAlgorithmNameBlock().equals("GOST28147"))
            {
                return new IESParameterSpec(null, null, 256, 256, nonce);
            }

            return new IESParameterSpec(null, null, 128, 128, nonce);
        }
    }
}
