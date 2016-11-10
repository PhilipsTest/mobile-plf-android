/*
 * Copyright (c) 2016. Philips Electronics India Ltd
 * All rights reserved. Reproduction in whole or in part is prohibited without
 * the written consent of the copyright holder.
 */

package cdp.philips.com.mydemoapp.database;

import android.support.annotation.NonNull;
import android.util.Log;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.Where;
import com.philips.platform.core.datatypes.Consent;
import com.philips.platform.core.datatypes.ConsentDetailType;
import com.philips.platform.core.datatypes.Moment;
import com.philips.platform.core.datatypes.MomentType;
import com.philips.platform.core.dbinterfaces.DBFetchingInterface;
import com.philips.platform.core.events.ConsentBackendSaveResponse;
import com.philips.platform.datasync.consent.UCoreConsentDetail;

import java.net.HttpURLConnection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import cdp.philips.com.mydemoapp.database.table.OrmConsent;
import cdp.philips.com.mydemoapp.database.table.OrmConsentDetail;
import cdp.philips.com.mydemoapp.database.table.OrmConsentDetailType;
import cdp.philips.com.mydemoapp.database.table.OrmMoment;
import cdp.philips.com.mydemoapp.database.table.OrmSynchronisationData;
import cdp.philips.com.mydemoapp.listener.DBChangeListener;
import cdp.philips.com.mydemoapp.listener.EventHelper;
import cdp.philips.com.mydemoapp.temperature.TemperatureMomentHelper;

/**
 * (C) Koninklijke Philips N.V., 2015.
 * All rights reserved.
 */
@SuppressWarnings({"rawtypes", "unchecked"})
public class OrmFetchingInterfaceImpl implements DBFetchingInterface{

    static final String SYNCED_FIELD = "synced";


    @NonNull
    private Dao<OrmMoment, Integer> momentDao;

    @NonNull
    private Dao<OrmSynchronisationData, Integer> synchronisationDataDao;
    private final Dao<OrmConsent,Integer> consentDao;
    private final Dao<OrmConsentDetail, Integer> consentDetailsDao ;
    private final Dao<OrmConsentDetailType, Integer> consentDetailTypeDao;

    private TemperatureMomentHelper mTemperatureMomentHelper;


    public OrmFetchingInterfaceImpl(final @NonNull Dao<OrmMoment, Integer> momentDao,
                                    final @NonNull Dao<OrmSynchronisationData, Integer> synchronisationDataDao, Dao<OrmConsent, Integer> consentDao, Dao<OrmConsentDetail, Integer> consentDetailsDao, Dao<OrmConsentDetailType, Integer> consentDetailTypeDao) {
        this.momentDao = momentDao;
        this.synchronisationDataDao = synchronisationDataDao;
        mTemperatureMomentHelper = new TemperatureMomentHelper();

        this.consentDao = consentDao;
        this.consentDetailsDao = consentDetailsDao;
        this.consentDetailTypeDao = consentDetailTypeDao;
    }

    @Override
    public void fetchMoments() throws SQLException {
        QueryBuilder<OrmMoment, Integer> queryBuilder = momentDao.queryBuilder();
            getActiveMoments(momentDao.query(queryBuilder.prepare()));
        }

    @Override
    public void fetchConsents() throws SQLException {
        QueryBuilder<OrmConsent, Integer> queryBuilder = consentDao.queryBuilder();
        ArrayList<OrmConsent> ormConsents =(ArrayList<OrmConsent>)consentDao.query(queryBuilder.prepare());
        notifySucessConsentChange(ormConsents);
    }

    private void notifySucessConsentChange(ArrayList<? extends OrmConsent> ormConsents) {
        Map<Integer, ArrayList<DBChangeListener>> eventMap = EventHelper.getInstance().getEventMap();
        Set<Integer> integers = eventMap.keySet();
        if (integers.contains(EventHelper.CONSENT)) {
            ArrayList<DBChangeListener> dbChangeListeners = EventHelper.getInstance().getEventMap().get(EventHelper.CONSENT);
            for (DBChangeListener listener : dbChangeListeners) {
                if (ormConsents.size() != 0) {
                    listener.onSuccess(ormConsents.get(0));
                } else {
                    listener.onSuccess(null);
                }
            }
        }
    }

    @Override
    public void fetchMoments(@NonNull final MomentType type) throws SQLException {
        Log.i("***SPO***","In fetchMoments - OrmFetchingInterfaceImpl");
        final QueryBuilder<OrmMoment, Integer> queryBuilder = momentDao.queryBuilder();
        queryBuilder.orderBy("dateTime", true);
        getActiveMoments(momentDao.queryForEq("type_id", type.getId()));
    }

    @Override
    public void fetchMoments(@NonNull final MomentType... types) throws SQLException{
        List<OrmMoment> ormMoments = new ArrayList<OrmMoment>();
        List<Integer> ids = new ArrayList<>(types.length);
        final int i = 0;
        for (MomentType momentType : types) {
            ids.add(momentType.getId());
        }
        final QueryBuilder<OrmMoment, Integer> queryBuilder = momentDao.queryBuilder();
        queryBuilder.where().in("type_id", ids);
        queryBuilder.orderBy("dateTime", true);
        getActiveMoments(momentDao.query(queryBuilder.prepare()));
    }

    @Override
    public void fetchLastMoment(final MomentType type) throws SQLException{
        QueryBuilder<OrmMoment, Integer> builder = momentDao.queryBuilder();
        Where<OrmMoment, Integer> where = builder.where();
        where.eq("type_id", type.getId());
        builder.setWhere(where);
        builder.orderBy("dateTime", false);

        OrmMoment ormMoments = momentDao.queryForFirst(builder.prepare());
        ArrayList<OrmMoment> moments = new ArrayList<>();
        moments.add(ormMoments);

        mTemperatureMomentHelper.notifySuccessToAll(moments);
    }

    @Override
    public Object fetchMomentByGuid(@NonNull final String guid) throws SQLException{
        QueryBuilder<OrmSynchronisationData, Integer> syncDataQueryBuilder = getSyncQueryBuilderWithGuidFilter(guid);

        QueryBuilder<OrmMoment, Integer> momentQueryBuilder = momentDao.queryBuilder();
        return momentQueryBuilder.join(syncDataQueryBuilder).queryForFirst();
    }

    @NonNull
    private QueryBuilder<OrmSynchronisationData, Integer> getSyncQueryBuilderWithGuidFilter(final @NonNull String guid) throws SQLException {
        QueryBuilder<OrmSynchronisationData, Integer> syncDataQueryBuilder = synchronisationDataDao.queryBuilder();

        syncDataQueryBuilder.where().eq("guid", guid);
        return syncDataQueryBuilder;
    }

    @Override
    public List<?> fetchNonSynchronizedMoments() throws SQLException{
        Log.i("***SPO***","In OrmFetchingInterfaceImpl fetchNonSynchronizedMoments");
        QueryBuilder<OrmMoment, Integer> momentQueryBuilder = momentDao.queryBuilder();
        Log.i("***SPO***","In OrmFetchingInterfaceImpl after query builder");
        momentQueryBuilder.where().eq(SYNCED_FIELD, false);
        Log.i("***SPO***","In OrmFetchingInterfaceImpl after where and before query");
        return momentQueryBuilder.query();
    }

    @Override
    public Object fetchMomentById(final int id) throws SQLException{
        QueryBuilder<OrmMoment, Integer> momentQueryBuilder = momentDao.queryBuilder();
        momentQueryBuilder.where().eq("id", id);

        return momentQueryBuilder.queryForFirst();
    }

    public void getActiveMoments(final List<?> ormMoments) {
        Log.i("***SPO***","In getActiveMoments - OrmFetchingInterfaceImpl");
        List<OrmMoment> activeOrmMoments = new ArrayList<>();
        if (ormMoments != null) {
            for (OrmMoment ormMoment : (List<OrmMoment>)ormMoments) {
                if (ormMoment.getSynchronisationData() == null || !ormMoment.getSynchronisationData().isInactive()) {
                    activeOrmMoments.add(ormMoment);
                }
            }
        }
        Log.i("***SPO***","In getActiveMoments - OrmFetchingInterfaceImpl and ormMoments = " + ormMoments);
        mTemperatureMomentHelper.notifySuccessToAll((ArrayList<? extends Object>) ormMoments);
    }

    @Override
    public Map<Class, List<?>> putMomentsForSync(final Map<Class, List<?>> dataToSync) throws SQLException {
        Log.i("***SPO***","In OrmFetchingInterfaceImpl before fetchNonSynchronizedMoments");
        List<? extends Moment> ormMomentList = (List<? extends Moment>)fetchNonSynchronizedMoments();
        Log.i("***SPO***","In OrmFetchingInterfaceImpl dataToSync.put");
        dataToSync.put(Moment.class, ormMomentList);
        return dataToSync;
    }

    @Override
    public Map<Class, List<?>> putConsentForSync(Map<Class, List<?>> dataToSync) throws SQLException {
        List<? extends Consent> consentList = fetchNonSynchronizedConsents();
        dataToSync.put(Consent.class, consentList);
        return dataToSync;
    }

    public List<OrmConsent> fetchNonSynchronizedConsents() throws SQLException {
        QueryBuilder<OrmConsent, Integer> consentQueryBuilder = consentDao.queryBuilder();
        consentQueryBuilder.where().eq("beSynchronized", false);

        return consentQueryBuilder.query();
    }

    public OrmConsent fetchConsentByCreatorId(@NonNull final String creatorId) throws SQLException {
        QueryBuilder<OrmConsent, Integer> consentQueryBuilder = consentDao.queryBuilder();
        consentQueryBuilder.where().eq("creatorId", creatorId);

        return consentQueryBuilder.queryForFirst();
    }
}
