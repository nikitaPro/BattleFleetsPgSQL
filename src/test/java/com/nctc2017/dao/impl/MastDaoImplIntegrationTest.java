package com.nctc2017.dao.impl;


import com.nctc2017.bean.Mast;
import com.nctc2017.configuration.ApplicationConfig;
import com.nctc2017.constants.DatabaseObject;
import com.nctc2017.dao.MastDao;
import com.nctc2017.dao.ShipDao;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigInteger;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration(classes = {ApplicationConfig.class})
@Transactional
public class MastDaoImplIntegrationTest {

    @Autowired
    MastDao mastDao;

    @Autowired
    ShipDao shipDao;

    private static final BigInteger TEST_MASt_OBJECT_ID_INCORRECT = BigInteger.valueOf(12);
    private static final BigInteger EXC_VALUE = new BigInteger("9223372036854775809");
    private static final String NAME_OF_MAST1_TEMPLATE = "Sprit topmast";

    @Test

    @Rollback(true)
    public void testDaoFinding() {
        // given

        BigInteger id = mastDao.createNewMast(DatabaseObject.MAST1_TEMPLATE_OBJECT_ID, null);
        //when TODO
        Mast mast = mastDao.findMast(id);
        //then
        assertTrue(mast.getCost() > 0);
        assertTrue(mast.getCurSpeed() > 0);
        assertTrue(mast.getMaxSpeed() > 0);
        assertEquals(NAME_OF_MAST1_TEMPLATE, mast.getTemplateName());
    }

    @Test
    @Rollback(true)
    public void testDaoDeleting() {
        BigInteger id = mastDao.createNewMast(DatabaseObject.MAST1_TEMPLATE_OBJECT_ID, null);
        mastDao.deleteMast(id);
    }

    @Test(expected = IllegalArgumentException.class)
    @Rollback(true)
    public void testDaoDeletingIllegalArgument() {
        mastDao.deleteMast(TEST_MASt_OBJECT_ID_INCORRECT);
    }

    @Test
    @Rollback(true)
    public void updateMastSpeed() {
        int newSpeed = 4;

        BigInteger id = mastDao.createNewMast(DatabaseObject.MAST1_TEMPLATE_OBJECT_ID, null);
        mastDao.updateCurMastSpeed(id, newSpeed);

        assertEquals(newSpeed, mastDao.findMast(id).getCurSpeed());
    }

    @Test(expected = ArithmeticException.class)
    @Rollback(true)
    public void testDaoDeletingArithmeticalException() {
        mastDao.deleteMast(EXC_VALUE);
    }

    @Test
    @Rollback(true)
    public void testDaoCreating() {
        // Given
        BigInteger createdId = mastDao.createNewMast(DatabaseObject.MAST1_TEMPLATE_OBJECT_ID,
                shipDao.createNewShip(DatabaseObject.T_CARAVELLA_OBJECT_ID, null));
        //when
        Mast mast = mastDao.findMast(createdId);
        //then
        assertTrue(mast.getCost() > 0);
        assertTrue(mast.getCurSpeed() > 0);
        assertTrue(mast.getMaxSpeed() > 0);
        assertEquals(NAME_OF_MAST1_TEMPLATE, mast.getTemplateName());
    }


}
