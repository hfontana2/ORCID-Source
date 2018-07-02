package org.orcid.core.manager.v3;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mock;
import org.orcid.core.BaseTest;
import org.orcid.jaxb.model.v3.rc1.common.Day;
import org.orcid.jaxb.model.v3.rc1.common.DisambiguatedOrganization;
import org.orcid.jaxb.model.v3.rc1.common.FuzzyDate;
import org.orcid.jaxb.model.v3.rc1.common.Iso3166Country;
import org.orcid.jaxb.model.v3.rc1.common.Month;
import org.orcid.jaxb.model.v3.rc1.common.Organization;
import org.orcid.jaxb.model.v3.rc1.common.OrganizationAddress;
import org.orcid.jaxb.model.v3.rc1.common.Visibility;
import org.orcid.jaxb.model.v3.rc1.common.Year;
import org.orcid.jaxb.model.v3.rc1.record.Affiliation;
import org.orcid.jaxb.model.v3.rc1.record.AffiliationType;
import org.orcid.jaxb.model.v3.rc1.record.Distinction;
import org.orcid.jaxb.model.v3.rc1.record.Education;
import org.orcid.jaxb.model.v3.rc1.record.Employment;
import org.orcid.jaxb.model.v3.rc1.record.ExternalID;
import org.orcid.jaxb.model.v3.rc1.record.ExternalIDs;
import org.orcid.jaxb.model.v3.rc1.record.InvitedPosition;
import org.orcid.jaxb.model.v3.rc1.record.Membership;
import org.orcid.jaxb.model.v3.rc1.record.Qualification;
import org.orcid.jaxb.model.v3.rc1.record.Service;
import org.orcid.jaxb.model.v3.rc1.record.summary.AffiliationGroup;
import org.orcid.jaxb.model.v3.rc1.record.summary.AffiliationSummary;
import org.orcid.jaxb.model.v3.rc1.record.summary.DistinctionSummary;
import org.orcid.jaxb.model.v3.rc1.record.summary.EducationSummary;
import org.orcid.jaxb.model.v3.rc1.record.summary.EmploymentSummary;
import org.orcid.jaxb.model.v3.rc1.record.summary.InvitedPositionSummary;
import org.orcid.jaxb.model.v3.rc1.record.summary.MembershipSummary;
import org.orcid.jaxb.model.v3.rc1.record.summary.QualificationSummary;
import org.orcid.jaxb.model.v3.rc1.record.summary.ServiceSummary;
import org.orcid.persistence.dao.OrgAffiliationRelationDao;
import org.orcid.persistence.jpa.entities.ClientDetailsEntity;
import org.orcid.persistence.jpa.entities.OrgAffiliationRelationEntity;
import org.orcid.persistence.jpa.entities.SourceEntity;
import org.orcid.test.TargetProxyHelper;

public class AffiliationsManagerTest extends BaseTest {
    private static final List<String> DATA_FILES = Arrays.asList("/data/SecurityQuestionEntityData.xml", "/data/SourceClientDetailsEntityData.xml",
            "/data/ProfileEntityData.xml", "/data/ClientDetailsEntityData.xml", "/data/OrgsEntityData.xml", "/data/OrgAffiliationEntityData.xml", "/data/RecordNameEntityData.xml");
    
    private static final String CLIENT_1_ID = "4444-4444-4444-4498";
    private String claimedOrcid = "0000-0000-0000-0002";
    private String unclaimedOrcid = "0000-0000-0000-0001";
    
    @Mock
    private SourceManager sourceManager;
    
    @Resource(name = "affiliationsManagerV3")
    private AffiliationsManager affiliationsManager;
    
    @Resource(name = "orgAffiliationRelationDaoReadOnly")
    private OrgAffiliationRelationDao orgAffiliationRelationDao;
    
    @BeforeClass
    public static void initDBUnitData() throws Exception {
        initDBUnitData(DATA_FILES);
    }

    @Before
    public void before() {
        TargetProxyHelper.injectIntoProxy(affiliationsManager, "sourceManager", sourceManager);        
    }
    
    @AfterClass
    public static void removeDBUnitData() throws Exception {
        List<String> reversedDataFiles = new ArrayList<String>(DATA_FILES);
        Collections.reverse(reversedDataFiles);
        removeDBUnitData(reversedDataFiles);
    }    
    
    @Test
    public void testGetGroupedAffiliations() {
        String orcid = "0000-0000-0000-0003";
        Map<AffiliationType, List<AffiliationGroup<AffiliationSummary>>> map = affiliationsManager.getGroupedAffiliations(orcid, false);
        assertNotNull(map);
        System.out.println("- Entities! --------------------------------------------------------------------------------------------****");
        System.out.println("--------------------------------------------------------------------------------------------****");
        System.out.println("--------------------------------------------------------------------------------------------****");            
        for(OrgAffiliationRelationEntity e : orgAffiliationRelationDao.getByUser(orcid)) {
            System.out.println(e.getId() + " -----> " + e.getExternalIdentifiersJson());                    
        }
        System.out.println("--------------------------------------------------------------------------------------------****");
        System.out.println("--------------------------------------------------------------------------------------------****");
        System.out.println("--------------------------------------------------------------------------------------------****");
        System.out.println();
        System.out.println();
        
        // Check distinctions
        assertTrue(map.containsKey(AffiliationType.DISTINCTION));
        List<AffiliationGroup<AffiliationSummary>> groups = map.get(AffiliationType.DISTINCTION);
        assertNotNull(groups);
        
        boolean found1 = false, found2 = false, found3 = false, found4 = false;
        for(AffiliationGroup<AffiliationSummary> g : groups) {
            AffiliationSummary element0 = g.getActivities().get(0);
            
                System.out.println("- Affiliation summary ----------------------------------------------------------------------****");
                for (ExternalID extId : g.getIdentifiers().getExternalIdentifier()) {
                    System.out.println(element0.getPutCode() + " -----> " + extId.getGroupId() + " " + extId.getType() + " " + extId.getValue());
                }
                System.out.println("--------------------------------------------------------------------------------------------****");                
        }
        
        assertTrue(found1);
        assertTrue(found2);
        assertTrue(found3);
        assertTrue(found4);
        
        found1 = found2 = found3 = found4 = false;
            
        // Check educations
        assertTrue(map.containsKey(AffiliationType.EDUCATION));
        groups = map.get(AffiliationType.EDUCATION);
        assertNotNull(groups);
        assertEquals(4, groups.size());
        
        for(AffiliationGroup<AffiliationSummary> g : groups) {
            AffiliationSummary element0 = g.getActivities().get(0);
            Long putCode = element0.getPutCode();
            if(putCode.equals(25L)) {
                assertEquals(2, g.getActivities().size());
                assertEquals(Long.valueOf(20), g.getActivities().get(1).getPutCode());
                found1 = true;
            } else if(putCode.equals(21L)) {
                assertEquals(1, g.getActivities().size());
                found2 = true;
            } else if(putCode.equals(22L)) {
                assertEquals(1, g.getActivities().size());
                found3 = true;
            } else if(putCode.equals(26L)) {
                assertEquals(1, g.getActivities().size());
                found4 = true;
            } else {
                fail("Invalid put code found:  " + putCode);
            }
        }
        
        assertTrue(found1);
        assertTrue(found2);
        assertTrue(found3);
        assertTrue(found4);
        
        found1 = found2 = found3 = found4 = false;
        
        // Check employments
        assertTrue(map.containsKey(AffiliationType.EMPLOYMENT));
        groups = map.get(AffiliationType.EMPLOYMENT);
        assertNotNull(groups);
        assertEquals(4, groups.size());
        
        for(AffiliationGroup<AffiliationSummary> g : groups) {
            AffiliationSummary element0 = g.getActivities().get(0);
            Long putCode = element0.getPutCode();
            if(putCode.equals(23L)) {
                assertEquals(2, g.getActivities().size());
                assertEquals(Long.valueOf(17), g.getActivities().get(1).getPutCode());
                found1 = true;
            } else if(putCode.equals(18L)) {
                assertEquals(1, g.getActivities().size());
                found2 = true;
            } else if(putCode.equals(19L)) {
                assertEquals(1, g.getActivities().size());
                found3 = true;
            } else if(putCode.equals(24L)) {
                assertEquals(1, g.getActivities().size());
                found4 = true;
            } else {
                fail("Invalid put code found:  " + putCode);
            }
        }
        
        assertTrue(found1);
        assertTrue(found2);
        assertTrue(found3);
        assertTrue(found4);
        
        found1 = found2 = found3 = found4 = false;
        
        // Check invited positions
        assertTrue(map.containsKey(AffiliationType.INVITED_POSITION));
        groups = map.get(AffiliationType.INVITED_POSITION);
        assertNotNull(groups);
        assertEquals(4, groups.size());
        
        for(AffiliationGroup<AffiliationSummary> g : groups) {
            AffiliationSummary element0 = g.getActivities().get(0);
            Long putCode = element0.getPutCode();
            if(putCode.equals(35L)) {
                assertEquals(2, g.getActivities().size());
                assertEquals(Long.valueOf(32), g.getActivities().get(1).getPutCode());
                found1 = true;
            } else if(putCode.equals(33L)) {
                assertEquals(1, g.getActivities().size());
                found2 = true;
            } else if(putCode.equals(34L)) {
                assertEquals(1, g.getActivities().size());
                found3 = true;
            } else if(putCode.equals(36L)) {
                assertEquals(1, g.getActivities().size());
                found4 = true;
            } else {
                fail("Invalid put code found:  " + putCode);
            }
        }
        
        assertTrue(found1);
        assertTrue(found2);
        assertTrue(found3);
        assertTrue(found4);
        
        found1 = found2 = found3 = found4 = false;
        
        // Check memberships
        assertTrue(map.containsKey(AffiliationType.MEMBERSHIP));
        groups = map.get(AffiliationType.MEMBERSHIP);
        assertNotNull(groups);
        assertEquals(4, groups.size());
        
        for(AffiliationGroup<AffiliationSummary> g : groups) {
            AffiliationSummary element0 = g.getActivities().get(0);
            Long putCode = element0.getPutCode();
            if(putCode.equals(40L)) {
                assertEquals(2, g.getActivities().size());
                assertEquals(Long.valueOf(37), g.getActivities().get(1).getPutCode());
                found1 = true;
            } else if(putCode.equals(38L)) {
                assertEquals(1, g.getActivities().size());
                found2 = true;
            } else if(putCode.equals(39L)) {
                assertEquals(1, g.getActivities().size());
                found3 = true;
            } else if(putCode.equals(41L)) {
                assertEquals(1, g.getActivities().size());
                found4 = true;
            } else {
                fail("Invalid put code found:  " + putCode);
            }
        }
        
        assertTrue(found1);
        assertTrue(found2);
        assertTrue(found3);
        assertTrue(found4);
        
        found1 = found2 = found3 = found4 = false;        
        
        // Check qualifications
        assertTrue(map.containsKey(AffiliationType.QUALIFICATION));
        groups = map.get(AffiliationType.QUALIFICATION);
        assertNotNull(groups);
        assertEquals(4, groups.size());
        
        for(AffiliationGroup<AffiliationSummary> g : groups) {
            AffiliationSummary element0 = g.getActivities().get(0);
            Long putCode = element0.getPutCode();
            if(putCode.equals(45L)) {
                assertEquals(2, g.getActivities().size());
                assertEquals(Long.valueOf(42), g.getActivities().get(1).getPutCode());
                found1 = true;
            } else if(putCode.equals(43L)) {
                assertEquals(1, g.getActivities().size());
                found2 = true;
            } else if(putCode.equals(44L)) {
                assertEquals(1, g.getActivities().size());
                found3 = true;
            } else if(putCode.equals(46L)) {
                assertEquals(1, g.getActivities().size());
                found4 = true;
            } else {
                fail("Invalid put code found:  " + putCode);
            }
        }
        
        assertTrue(found1);
        assertTrue(found2);
        assertTrue(found3);
        assertTrue(found4);
        
        found1 = found2 = found3 = found4 = false;
        
        // Check services
        assertTrue(map.containsKey(AffiliationType.SERVICE));
        groups = map.get(AffiliationType.SERVICE);
        assertNotNull(groups);
        assertEquals(4, groups.size());
        
        for(AffiliationGroup<AffiliationSummary> g : groups) {
            AffiliationSummary element0 = g.getActivities().get(0);
            Long putCode = element0.getPutCode();
            if(putCode.equals(50L)) {
                assertEquals(2, g.getActivities().size());
                assertEquals(Long.valueOf(47), g.getActivities().get(1).getPutCode());
                found1 = true;
            } else if(putCode.equals(48L)) {
                assertEquals(1, g.getActivities().size());
                found2 = true;
            } else if(putCode.equals(49L)) {
                assertEquals(1, g.getActivities().size());
                found3 = true;
            } else if(putCode.equals(51L)) {
                assertEquals(1, g.getActivities().size());
                found4 = true;
            } else {
                fail("Invalid put code found:  " + putCode);
            }
        }
        
        assertTrue(found1);
        assertTrue(found2);
        assertTrue(found3);
        assertTrue(found4);
        
        found1 = found2 = found3 = found4 = false;
    }
    
    private ExternalID getExternalID(String type, String value) {
        ExternalID externalID = new ExternalID();
        externalID.setType(type);
        externalID.setValue(value);
        return externalID;
    }
    
    private Distinction getDistinction() {
        Distinction element = new Distinction();
        fillAffiliation(element);
        return element;
    }
    
    private DistinctionSummary getDistinctionSummary() {
        DistinctionSummary element = new DistinctionSummary();
        fillAffiliationSummary(element);
        return element;
    }
    
    private Education getEducation() {
        Education element = new Education();
        fillAffiliation(element);
        return element;
    }
    
    private EducationSummary getEducationSummary() {
        EducationSummary element = new EducationSummary();
        fillAffiliationSummary(element);
        return element;
    }
    
    private Employment getEmployment() {
        Employment element = new Employment();
        fillAffiliation(element);
        return element;
    }
    
    private EmploymentSummary getEmploymentSummary() {
        EmploymentSummary element = new EmploymentSummary();
        fillAffiliationSummary(element);
        return element;
    }
    
    private InvitedPosition getInvitedPosition() {
        InvitedPosition element = new InvitedPosition();
        fillAffiliation(element);
        return element;
    }
    
    private InvitedPositionSummary getInvitedPositionSummary() {
        InvitedPositionSummary element = new InvitedPositionSummary();
        fillAffiliationSummary(element);
        return element;
    }
    
    private Membership getMembership() {
        Membership element = new Membership();
        fillAffiliation(element);
        return element;
    }
    
    private MembershipSummary getMembershipSummary() {
        MembershipSummary element = new MembershipSummary();
        fillAffiliationSummary(element);
        return element;
    }
    
    private Qualification getQualification() {
        Qualification element = new Qualification();
        fillAffiliation(element);
        return element;
    }
    
    private QualificationSummary getQualificationSummary() {
        QualificationSummary element = new QualificationSummary();
        fillAffiliationSummary(element);
        return element;
    }
    
    private Service getService() {
        Service element = new Service();
        fillAffiliation(element);
        return element;
    }
    
    private ServiceSummary getServiceSummary() {
        ServiceSummary element = new ServiceSummary();
        fillAffiliationSummary(element);
        return element;
    }
    
    private void fillAffiliation(Affiliation aff) {
        Organization org = new Organization();
        org.setName("org-name");
        OrganizationAddress address = new OrganizationAddress();
        address.setCity("city");
        address.setCountry(Iso3166Country.US);
        org.setAddress(address);
        DisambiguatedOrganization disambiguatedOrg = new DisambiguatedOrganization();
        disambiguatedOrg.setDisambiguatedOrganizationIdentifier("def456");
        disambiguatedOrg.setDisambiguationSource("WDB");
        org.setDisambiguatedOrganization(disambiguatedOrg);
        aff.setOrganization(org);
        aff.setStartDate(new FuzzyDate(new Year(2016), new Month(3), new Day(29)));
        aff.setVisibility(Visibility.PUBLIC);
    }
    
    private void fillAffiliationSummary(AffiliationSummary aff) {
        Organization org = new Organization();
        org.setName("org-name");
        OrganizationAddress address = new OrganizationAddress();
        address.setCity("city");
        address.setCountry(Iso3166Country.US);
        org.setAddress(address);
        DisambiguatedOrganization disambiguatedOrg = new DisambiguatedOrganization();
        disambiguatedOrg.setDisambiguatedOrganizationIdentifier("def456");
        disambiguatedOrg.setDisambiguationSource("WDB");
        org.setDisambiguatedOrganization(disambiguatedOrg);
        aff.setOrganization(org);
        aff.setStartDate(new FuzzyDate(new Year(2016), new Month(3), new Day(29)));
        aff.setVisibility(Visibility.PUBLIC);
    } 
}
