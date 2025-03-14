package org.bouncycastle.crypto.modes;

import org.bouncycastle.crypto.BlockCipher;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.DataLengthException;
import org.bouncycastle.crypto.StreamBlockCipher;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.crypto.params.ParametersWithIV;
import org.bouncycastle.crypto.params.ParametersWithRandom;
import org.bouncycastle.crypto.params.ParametersWithSBox;

/**
 * An implementation of the GOST CFB mode with CryptoPro key meshing as described in RFC 4357.
 */
public class GCFBBlockCipher
    extends StreamBlockCipher
{
    private static final byte[] C =
        {
            0x69, 0x00, 0x72, 0x22, 0x64, (byte)0xC9, 0x04, 0x23,
            (byte)0x8D, 0x3A, (byte)0xDB, (byte)0x96, 0x46, (byte)0xE9, 0x2A, (byte)0xC4,
            0x18, (byte)0xFE, (byte)0xAC, (byte)0x94, 0x00, (byte)0xED, 0x07, 0x12,
            (byte)0xC0, (byte)0x86, (byte)0xDC, (byte)0xC2, (byte)0xEF, 0x4C, (byte)0xA9, 0x2B
        };

    private final CFBBlockCipher cfbEngine;

    private ParametersWithIV initParams;

    private KeyParameter key;
    private long counter = 0;
    private boolean forEncryption;

    public GCFBBlockCipher(BlockCipher engine)
    {
        super(engine);
        //TODO: Ensure the key size of the engine is 32 bits
        this.cfbEngine = new CFBBlockCipher(engine, engine.getBlockSize() * 8);
    }

    public void initBlock(boolean forEncryption, CipherParameters params)
        throws IllegalArgumentException
    {
        counter = 0;
        cfbEngine.initBlock(forEncryption, params);
        byte[] iv = null;

        this.forEncryption = forEncryption;

        if (params instanceof ParametersWithIV)
        {
            ParametersWithIV ivParams = (ParametersWithIV)params;
            params = ivParams.getParameters();
            iv = ivParams.getIV();
        }

        if (params instanceof ParametersWithRandom)
        {
            params = ((ParametersWithRandom)params).getParameters();
        }

        if (params instanceof ParametersWithSBox)
        {
            params = ((ParametersWithSBox)params).getParameters();
        }

        key = (KeyParameter)params;

        /* Pick up key/IV from parameters or most recent parameters */
        if (key == null && initParams != null)
        {
            key = (KeyParameter)initParams.getParameters();
        }
        if (iv == null && initParams != null)
        {
            iv = initParams.getIV();
        }
        else
        {
            iv = cfbEngine.getCurrentIV();
        }

        /* Save the initParameters */
        initParams = new ParametersWithIV(key, iv);
    }

    public String getAlgorithmNameBlock()
    {
        String name = cfbEngine.getAlgorithmNameBlock();
        return name.substring(0, name.indexOf('/')) + "/G" + name.substring(name.indexOf('/') + 1);
    }

    public int getBlockSize()
    {
        return cfbEngine.getBlockSize();
    }

    public int processBlock(byte[] in, int inOff, byte[] out, int outOff)
        throws DataLengthException, IllegalStateException
    {
        this.processBytes(in, inOff, cfbEngine.getBlockSize(), out, outOff);

        return cfbEngine.getBlockSize();
    }

    protected byte calculateByte(byte b)
    {
        if (counter > 0 && (counter & 1023) == 0)
        {
            BlockCipher base = cfbEngine.getUnderlyingCipher();

            base.initBlock(false, key);

            byte[] nextKey = new byte[32];
            int blockSize = base.getBlockSize();

            for (int i = 0; i < nextKey.length; i += blockSize)
            {
                base.processBlock(C, i, nextKey, i);
            }

            key = new KeyParameter(nextKey);

            base.initBlock(true, key);

            byte[] iv = cfbEngine.getCurrentIV();

            base.processBlock(iv, 0, iv, 0);

            cfbEngine.initBlock(forEncryption, new ParametersWithIV(key, iv));
        }

        counter++;

        return cfbEngine.calculateByte(b);
    }

    public void resetBlock()
    {
        counter = 0;
        if (initParams != null)
        {
            key = (KeyParameter)initParams.getParameters();
            cfbEngine.initBlock(forEncryption, initParams);
        }
        else
        {
            cfbEngine.resetBlock();
        }
    }


    @Override
    public void init(boolean forEncryption, CipherParameters params) throws IllegalArgumentException {
        initBlock(forEncryption, params);
    }

    @Override
    public String getAlgorithmName() {
        return getAlgorithmNameBlock();
    }

    @Override
    public void reset() {
        resetBlock();
    }
}
