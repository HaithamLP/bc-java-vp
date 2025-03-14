package org.bouncycastle.crypto.test;

import java.security.SecureRandom;
import java.util.Random;

import org.bouncycastle.crypto.BlockCipher;
import org.bouncycastle.crypto.BufferedBlockCipher;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.DefaultBufferedBlockCipher;
import org.bouncycastle.crypto.InvalidCipherTextException;
import org.bouncycastle.crypto.engines.AESEngine;
import org.bouncycastle.crypto.modes.CBCBlockCipher;
import org.bouncycastle.crypto.modes.CFBBlockCipher;
import org.bouncycastle.crypto.modes.CTRModeCipher;
import org.bouncycastle.crypto.modes.OFBBlockCipher;
import org.bouncycastle.crypto.modes.SICBlockCipher;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.crypto.params.ParametersWithIV;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.Strings;
import org.bouncycastle.util.encoders.Hex;
import org.bouncycastle.util.test.SimpleTest;

/**
 * Test vectors from the NIST standard tests and Brian Gladman's vector set
 * <a href="https://fp.gladman.plus.com/cryptography_technology/rijndael/">
 * https://fp.gladman.plus.com/cryptography_technology/rijndael/</a>
 */
public class AESTest
    extends CipherTest
{
    private static final byte[] tData   = Hex.decode("AAFE47EE82411A2BF3F6752AE8D7831138F041560631B114F3F6752AE8D7831138F041560631B1145A01020304050607");
    private static final byte[] outCBC1 = Hex.decode("a444a9a4d46eb30cb7ed34d62873a89f8fdf2bf8a54e1aeadd06fd85c9cb46f021ee7cd4f418fa0bb72e9d07c70d5d20");
    private static final byte[] outCBC2 = Hex.decode("585681354f0e01a86b32f94ebb6a675045d923cf201263c2aaecca2b4de82da0edd74ca5efd654c688f8a58e61955b11");
    private static final byte[] outSIC1 = Hex.decode("82a1744e8ebbd053ca72362d5e570326e0b6fdaf824ab673fbf029042886b23c75129a015852913790f81f94447475a0");
    private static final byte[] outSIC2 = Hex.decode("146cbb581d9e12c3333dd9c736fbb93043c92019f78580da48f81f80b3f551d58ea836fed480fc6912fefa9c5c89cc24");
    private static final byte[] outCFB1 = Hex.decode("82a1744e8ebbd053ca72362d5e5703264b4182de3208c374b8ac4fa36af9c5e5f4f87d1e3b67963d06acf5eb13914c90");
    private static final byte[] outCFB2 = Hex.decode("146cbb581d9e12c3333dd9c736fbb9303c8a3eb5185e2809e9d3c28e25cc2d2b6f5c11ee28d6530f72c412b1438a816a");
    private static final byte[] outOFB1 = Hex.decode("82a1744e8ebbd053ca72362d5e5703261ebf1fdbec05e57b3465b583132f84b43bf95b2c89040ad1677b22d42db69a7a");
    private static final byte[] outOFB2 = Hex.decode("146cbb581d9e12c3333dd9c736fbb9309ea4c2a7696c84959a2dada49f2f1c5905db1f0cec3a31acbc4701e74ab05e1f");

    static SimpleTest[]  tests = 
            {
                new BlockCipherVectorTest(0, AESEngine.newInstance(),
                        new KeyParameter(Hex.decode("80000000000000000000000000000000")),
                        "00000000000000000000000000000000", "0EDD33D3C621E546455BD8BA1418BEC8"),
                new BlockCipherVectorTest(1, AESEngine.newInstance(),
                        new KeyParameter(Hex.decode("00000000000000000000000000000080")),
                        "00000000000000000000000000000000", "172AEAB3D507678ECAF455C12587ADB7"),
                new BlockCipherMonteCarloTest(2, 10000, AESEngine.newInstance(),
                        new KeyParameter(Hex.decode("00000000000000000000000000000000")),
                        "00000000000000000000000000000000", "C34C052CC0DA8D73451AFE5F03BE297F"),
                new BlockCipherMonteCarloTest(3, 10000, AESEngine.newInstance(),
                        new KeyParameter(Hex.decode("5F060D3716B345C253F6749ABAC10917")),
                        "355F697E8B868B65B25A04E18D782AFA", "ACC863637868E3E068D2FD6E3508454A"),
                new BlockCipherVectorTest(4, AESEngine.newInstance(),
                        new KeyParameter(Hex.decode("000000000000000000000000000000000000000000000000")),
                        "80000000000000000000000000000000", "6CD02513E8D4DC986B4AFE087A60BD0C"),
                new BlockCipherMonteCarloTest(5, 10000, AESEngine.newInstance(),
                        new KeyParameter(Hex.decode("AAFE47EE82411A2BF3F6752AE8D7831138F041560631B114")),
                        "F3F6752AE8D7831138F041560631B114", "77BA00ED5412DFF27C8ED91F3C376172"),
                new BlockCipherVectorTest(6, AESEngine.newInstance(),
                        new KeyParameter(Hex.decode("0000000000000000000000000000000000000000000000000000000000000000")),
                        "80000000000000000000000000000000", "DDC6BF790C15760D8D9AEB6F9A75FD4E"),
                new BlockCipherMonteCarloTest(7, 10000, AESEngine.newInstance(),
                        new KeyParameter(Hex.decode("28E79E2AFC5F7745FCCABE2F6257C2EF4C4EDFB37324814ED4137C288711A386")),
                        "C737317FE0846F132B23C8C2A672CE22", "E58B82BFBA53C0040DC610C642121168"),
                new BlockCipherVectorTest(8, AESEngine.newInstance(),
                        new KeyParameter(Hex.decode("80000000000000000000000000000000")),
                        "00000000000000000000000000000000", "0EDD33D3C621E546455BD8BA1418BEC8"),
                new BlockCipherVectorTest(9, AESEngine.newInstance(),
                        new KeyParameter(Hex.decode("00000000000000000000000000000080")),
                        "00000000000000000000000000000000", "172AEAB3D507678ECAF455C12587ADB7"),
                new BlockCipherMonteCarloTest(10, 10000, AESEngine.newInstance(),
                        new KeyParameter(Hex.decode("00000000000000000000000000000000")),
                        "00000000000000000000000000000000", "C34C052CC0DA8D73451AFE5F03BE297F"),
                new BlockCipherMonteCarloTest(11, 10000, AESEngine.newInstance(),
                        new KeyParameter(Hex.decode("5F060D3716B345C253F6749ABAC10917")),
                        "355F697E8B868B65B25A04E18D782AFA", "ACC863637868E3E068D2FD6E3508454A"),
                new BlockCipherVectorTest(12, AESEngine.newInstance(),
                        new KeyParameter(Hex.decode("000000000000000000000000000000000000000000000000")),
                        "80000000000000000000000000000000", "6CD02513E8D4DC986B4AFE087A60BD0C"),
                new BlockCipherMonteCarloTest(13, 10000, AESEngine.newInstance(),
                        new KeyParameter(Hex.decode("AAFE47EE82411A2BF3F6752AE8D7831138F041560631B114")),
                        "F3F6752AE8D7831138F041560631B114", "77BA00ED5412DFF27C8ED91F3C376172"),
                new BlockCipherVectorTest(14, AESEngine.newInstance(),
                        new KeyParameter(Hex.decode("0000000000000000000000000000000000000000000000000000000000000000")),
                        "80000000000000000000000000000000", "DDC6BF790C15760D8D9AEB6F9A75FD4E"),
                new BlockCipherMonteCarloTest(15, 10000, AESEngine.newInstance(),
                        new KeyParameter(Hex.decode("28E79E2AFC5F7745FCCABE2F6257C2EF4C4EDFB37324814ED4137C288711A386")),
                        "C737317FE0846F132B23C8C2A672CE22", "E58B82BFBA53C0040DC610C642121168"),
                new BlockCipherVectorTest(16, AESEngine.newInstance(),
                        new KeyParameter(Hex.decode("80000000000000000000000000000000")),
                        "00000000000000000000000000000000", "0EDD33D3C621E546455BD8BA1418BEC8"),
                new BlockCipherVectorTest(17, AESEngine.newInstance(),
                        new KeyParameter(Hex.decode("00000000000000000000000000000080")),
                        "00000000000000000000000000000000", "172AEAB3D507678ECAF455C12587ADB7"),
                new BlockCipherMonteCarloTest(18, 10000, AESEngine.newInstance(),
                        new KeyParameter(Hex.decode("00000000000000000000000000000000")),
                        "00000000000000000000000000000000", "C34C052CC0DA8D73451AFE5F03BE297F"),
                new BlockCipherMonteCarloTest(19, 10000, AESEngine.newInstance(),
                        new KeyParameter(Hex.decode("5F060D3716B345C253F6749ABAC10917")),
                        "355F697E8B868B65B25A04E18D782AFA", "ACC863637868E3E068D2FD6E3508454A"),
                new BlockCipherVectorTest(20, AESEngine.newInstance(),
                        new KeyParameter(Hex.decode("000000000000000000000000000000000000000000000000")),
                        "80000000000000000000000000000000", "6CD02513E8D4DC986B4AFE087A60BD0C"),
                new BlockCipherMonteCarloTest(21, 10000, AESEngine.newInstance(),
                        new KeyParameter(Hex.decode("AAFE47EE82411A2BF3F6752AE8D7831138F041560631B114")),
                        "F3F6752AE8D7831138F041560631B114", "77BA00ED5412DFF27C8ED91F3C376172"),
                new BlockCipherVectorTest(22, AESEngine.newInstance(),
                        new KeyParameter(Hex.decode("0000000000000000000000000000000000000000000000000000000000000000")),
                        "80000000000000000000000000000000", "DDC6BF790C15760D8D9AEB6F9A75FD4E"),
                new BlockCipherMonteCarloTest(23, 10000, AESEngine.newInstance(),
                        new KeyParameter(Hex.decode("28E79E2AFC5F7745FCCABE2F6257C2EF4C4EDFB37324814ED4137C288711A386")),
                        "C737317FE0846F132B23C8C2A672CE22", "E58B82BFBA53C0040DC610C642121168")
            };
    
    private BlockCipher _engine = AESEngine.newInstance();

    public AESTest()
    {
        super(tests, AESEngine.newInstance(), new KeyParameter(new byte[16]));
    }

    public String getName()
    {
        return "AES";
    }

    private void testNullSIC()
        throws InvalidCipherTextException
    {
        BufferedBlockCipher b = new DefaultBufferedBlockCipher(SICBlockCipher.newInstance(AESEngine.newInstance()));
        KeyParameter kp = new KeyParameter(Hex.decode("5F060D3716B345C253F6749ABAC10917"));

        b.init(true, new ParametersWithIV(kp, new byte[16]));

        byte[] out = new byte[b.getOutputSize(tData.length)];

        int len = b.processBytes(tData, 0, tData.length, out, 0);

        len += b.doFinal(out, len);

        if (!areEqual(outSIC1, out))
        {
            fail("no match on first nullSIC check");
        }

        b.init(true, new ParametersWithIV(null, Hex.decode("000102030405060708090a0b0c0d0e0f")));

        len = b.processBytes(tData, 0, tData.length, out, 0);

        len += b.doFinal(out, len);

        if (!areEqual(outSIC2, out))
        {
            fail("no match on second nullSIC check");
        }
    }

    private void testNullCBC()
        throws InvalidCipherTextException
    {
        BufferedBlockCipher b = new DefaultBufferedBlockCipher(new CBCBlockCipher(AESEngine.newInstance()));
        KeyParameter kp = new KeyParameter(Hex.decode("5F060D3716B345C253F6749ABAC10917"));

        b.init(true, new ParametersWithIV(kp, new byte[16]));

        byte[] out = new byte[b.getOutputSize(tData.length)];

        int len = b.processBytes(tData, 0, tData.length, out, 0);

        len += b.doFinal(out, len);

        if (!areEqual(outCBC1, out))
        {
            fail("no match on first nullCBC check");
        }

        b.init(true, new ParametersWithIV(null, Hex.decode("000102030405060708090a0b0c0d0e0f")));

        len = b.processBytes(tData, 0, tData.length, out, 0);

        len += b.doFinal(out, len);

        if (!areEqual(outCBC2, out))
        {
            fail("no match on second nullCBC check");
        }
    }

    private void testNullOFB()
        throws InvalidCipherTextException
    {
        BufferedBlockCipher b = new DefaultBufferedBlockCipher(new OFBBlockCipher(AESEngine.newInstance(), 128));
        KeyParameter kp = new KeyParameter(Hex.decode("5F060D3716B345C253F6749ABAC10917"));

        b.init(true, new ParametersWithIV(kp, new byte[16]));

        byte[] out = new byte[b.getOutputSize(tData.length)];

        int len = b.processBytes(tData, 0, tData.length, out, 0);

        len += b.doFinal(out, len);

        if (!areEqual(outOFB1, out))
        {
            fail("no match on first nullOFB check");
        }

        b.init(true, new ParametersWithIV(null, Hex.decode("000102030405060708090a0b0c0d0e0f")));

        len = b.processBytes(tData, 0, tData.length, out, 0);

        len += b.doFinal(out, len);

        if (!areEqual(outOFB2, out))
        {
            fail("no match on second nullOFB check");
        }
    }

    private void testNullCFB()
        throws InvalidCipherTextException
    {
        BufferedBlockCipher b = new DefaultBufferedBlockCipher(new CFBBlockCipher(AESEngine.newInstance(), 128));
        KeyParameter kp = new KeyParameter(Hex.decode("5F060D3716B345C253F6749ABAC10917"));

        b.init(true, new ParametersWithIV(kp, new byte[16]));

        byte[] out = new byte[b.getOutputSize(tData.length)];

        int len = b.processBytes(tData, 0, tData.length, out, 0);

        len += b.doFinal(out, len);

        if (!areEqual(outCFB1, out))
        {
            fail("no match on first nullCFB check");
        }

        b.init(true, new ParametersWithIV(null, Hex.decode("000102030405060708090a0b0c0d0e0f")));

        len = b.processBytes(tData, 0, tData.length, out, 0);

        len += b.doFinal(out, len);

        if (!areEqual(outCFB2, out))
        {
            fail("no match on second nullCFB check");
        }
    }

    private boolean areEqual(byte[] a, int aOff, byte[] b, int bOff)
    {
        for (int i = bOff; i != b.length; i++)
        {
            if (a[aOff + i - bOff] != b[i])
            {
                return false;
            }
        }

        return true;
    }

    private void skipTest()
    {
        CipherParameters params = new ParametersWithIV(new KeyParameter(Hex.decode("5F060D3716B345C253F6749ABAC10917")), Hex.decode("00000000000000000000000000000000"));
        CTRModeCipher engine = SICBlockCipher.newInstance(AESEngine.newInstance());

        engine.initBlock(true, params);

        SecureRandom rand = new SecureRandom();
        byte[]       plain = new byte[50000];
        byte[]       cipher = new byte[50000];

        rand.nextBytes(plain);
        engine.processBytes(plain, 0, plain.length, cipher, 0);

        byte[]      fragment = new byte[20];

        engine.initBlock(true, params);

        engine.skip(10);

        if (engine.getPosition() != 10)
        {
            fail("skip position incorrect - 10 got " + engine.getPosition());
        }

        engine.processBytes(plain, 10, fragment.length, fragment, 0);

        if (!areEqual(cipher, 10, fragment, 0))
        {
            fail("skip forward 10 failed");
        }

        engine.skip(1000);

        if (engine.getPosition() != 1010 + fragment.length)
        {
            fail("skip position incorrect - " + (1010 + fragment.length) + " got " + engine.getPosition());
        }

        engine.processBytes(plain, 1010 + fragment.length, fragment.length, fragment, 0);

        if (!areEqual(cipher, 1010 + fragment.length, fragment, 0))
        {
            fail("skip forward 1000 failed");
        }

        engine.skip(-10);

        if (engine.getPosition() != 1010 + 2 * fragment.length - 10)
        {
            fail("skip position incorrect - " + (1010 + 2 * fragment.length - 10) + " got " + engine.getPosition());
        }

        engine.processBytes(plain, 1010 + 2 * fragment.length - 10, fragment.length, fragment, 0);

        if (!areEqual(cipher, 1010 + 2 * fragment.length - 10, fragment, 0))
        {
            fail("skip back 10 failed");
        }

        engine.skip(-1000);

        if (engine.getPosition() != 60)
        {
            fail("skip position incorrect - " + 60 + " got " + engine.getPosition());
        }

        engine.processBytes(plain, 60, fragment.length, fragment, 0);

        if (!areEqual(cipher, 60, fragment, 0))
        {
            fail("skip back 1000 failed");
        }

        long pos = engine.seekTo(1010);

        if (pos != 1010)
        {
            fail("position incorrect - " + 1010 + " got " + pos);
        }

        engine.processBytes(plain, 1010, fragment.length, fragment, 0);

        if (!areEqual(cipher, 1010, fragment, 0))
        {
            fail("seek to 1010 failed");
        }

        engine.resetBlock();

        for (int i = 0; i != 5000; i++)
        {
            engine.skip(i);

            if (engine.getPosition() != i)
            {
                fail("skip forward at wrong position");
            }

            engine.processBytes(plain, i, fragment.length, fragment, 0);

            if (!areEqual(cipher, i, fragment, 0))
            {
                fail("skip forward i failed: " + i);
            }

            if (engine.getPosition() != i + fragment.length)
            {
                fail("cipher at wrong position: " + engine.getPosition() + " [" + i + "]");
            }

            engine.skip(-fragment.length);

            if (engine.getPosition() != i)
            {
                fail("skip back at wrong position");
            }

            engine.processBytes(plain, i, fragment.length, fragment, 0);

            if (!areEqual(cipher, i, fragment, 0))
            {
                fail("skip back i failed: " + i);
            }

            engine.resetBlock();
        }
    }

    private void ctrCounterTest()
    {
        CipherParameters params = new ParametersWithIV(new KeyParameter(Hex.decode("5F060D3716B345C253F6749ABAC10917")), Hex.decode("000000000000000000000000000000"));
        CTRModeCipher engine = SICBlockCipher.newInstance(AESEngine.newInstance());

        engine.initBlock(true, params);

        SecureRandom rand = new SecureRandom();
        byte[]       cipher = new byte[256 * 16 + 1];
        byte[]       plain = new byte[256 * 16];

        rand.nextBytes(plain);
        engine.processBytes(plain, 0, plain.length, cipher, 0);

        engine.initBlock(true, params);

        plain = new byte[256 * 16 + 1];
        engine.initBlock(true, params);

        try
        {
            engine.processBytes(plain, 0, plain.length, cipher, 0);
            fail("out of range data not caught");
        }
        catch (IllegalStateException e)
        {
            if (!"Counter in CTR/SIC mode out of range.".equals(e.getMessage()))
            {
                fail("wrong exception");
            }
        }
    }

    private void testLastByte()
        throws Exception
    {
        CTRModeCipher cipher = SICBlockCipher.newInstance(AESEngine.newInstance());
        byte[] iv = new byte[15];
        byte[] key = new byte[16];

        Arrays.fill(iv, (byte)0x0a);
        cipher.initBlock(true, new ParametersWithIV(new KeyParameter(key), iv));

        int lastBlock = 255; // the last block
        cipher.seekTo((lastBlock * 16));


        for (int t = 0; t < 15; t++)     // the last byte
        {
            cipher.returnByte((byte)0x00);
        }

        //
        // This should return the last byte
        //
        cipher.returnByte((byte)0);

        //
        // This should fail.
        //
        try
        {
            cipher.returnByte((byte)0);
            fail("should not succed");
        }
        catch (Exception ex)
        {
            isEquals(ex.getMessage(), "Counter in CTR/SIC mode out of range.");
        }
    }

    private void ctrFragmentedTest()
        throws InvalidCipherTextException
    {
        CTRModeCipher engine = SICBlockCipher.newInstance(AESEngine.newInstance());
        KeyParameter kp = new KeyParameter(Hex.decode("5F060D3716B345C253F6749ABAC10917"));

        byte[] out = new byte[tData.length];

        for (int fragmentLength = 1; fragmentLength < 16; ++fragmentLength)
        {
            byte[] fragment = new byte[fragmentLength];
            engine.initBlock(true, new ParametersWithIV(kp, new byte[16]));

            int inPos = 0, outPos = 0;
            do
            {
                int num = Math.min(fragmentLength, tData.length - inPos);
                System.arraycopy(tData, inPos, fragment, 0, num);
                outPos += engine.processBytes(fragment, 0, num, out, outPos);
                inPos += num;
            }
            while (inPos < tData.length);

            if (!areEqual(outSIC1, out))
            {
                fail("no match for fragmented check: " + fragmentLength);
            }
        }
    }

    public void performTest()
        throws Exception
    {
        super.performTest();

        byte[] keyBytes = new byte[16];
        
        _engine.initBlock(true, new KeyParameter(keyBytes));
        
        //
        // init tests
        //
        try
        {
            byte[]      dudKey = new byte[6];
            
            _engine.initBlock(true, new KeyParameter(dudKey));
            
            fail("failed key length check");
        }
        catch (IllegalArgumentException e)
        {
            // expected 
        }
        
        try
        {
            byte[]      iv = new byte[16];

            _engine.initBlock(true, new ParametersWithIV(null, iv));
            
            fail("failed parameter check");
        }
        catch (IllegalArgumentException e)
        {
            // expected 
        }

        testNullCBC();
        testNullSIC();
        testNullOFB();
        testNullCFB();

        skipTest();
        ctrCounterTest();
        ctrFragmentedTest();
        testLastByte();
        testCounter();
    }

    static byte[] fileBytes = new byte[0];
    static byte[] iv = new byte[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};

    private void testCounter() {
        Random random = new Random();
        for (int i = 0; i < 255; i++) {
            iv[iv.length - 1] += (byte) i;

            String inStr = " 1234567890jl字符串";
            for (int j = 1000; j > 0; j--) {
                inStr += (char) ('a' + random.nextInt(26));
                verify(inStr);
                fileBytes = new byte[0];
            }
        }
    }

    private void verify(String inStr) {
        SICBlockCipher cipher = newCipher();
        byte[] bytes = Strings.toUTF8ByteArray(inStr);

        appendFile(bytes, cipher);
        appendFile(bytes, cipher);
        appendFile(bytes, cipher);

        byte[] out = new byte[fileBytes.length];
        newCipher().processBytes(fileBytes, 0, fileBytes.length, out, 0);
        String outStr = Strings.fromUTF8ByteArray(out);

        if (!outStr.equals(inStr + inStr + inStr)) {
            throw new RuntimeException("fail");
        }
    }

    private void appendFile(byte[] bytes, SICBlockCipher cipher) {
        cipher.seekTo(fileBytes.length);
        byte[] out = new byte[bytes.length];
        cipher.processBytes(bytes, 0, bytes.length, out, 0);

        byte[] newFileBytes = Arrays.copyOf(fileBytes, fileBytes.length + out.length);
        System.arraycopy(out, 0, newFileBytes, fileBytes.length, out.length);
        fileBytes = newFileBytes;
    }

    private static SICBlockCipher newCipher() {
        SICBlockCipher sicBlockCipher = new SICBlockCipher(new AESEngine());
        byte[] key = "1234567890123456".getBytes();
        ParametersWithIV parametersWithIV = new ParametersWithIV(new KeyParameter(key), iv);
        sicBlockCipher.initBlock(true, parametersWithIV);
        return sicBlockCipher;
    }

    public static void main(
        String[]    args)
    {
        runTest(new AESTest());
    }
}
