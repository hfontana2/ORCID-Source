/**
 * =============================================================================
 *
 * ORCID (R) Open Source
 * http://orcid.org
 *
 * Copyright (c) 2012-2014 ORCID, Inc.
 * Licensed under an MIT-Style License (MIT)
 * http://orcid.org/open-source-license
 *
 * This copyright and license information (including a link to the full license)
 * shall be included in its entirety in all copies or substantial portion of
 * the software.
 *
 * =============================================================================
 */
package org.orcid.api.memberV2.server.delegator;

import javax.ws.rs.core.Response;

/**
 * 
 * @author Will Simpson
 * 
 */
public interface MemberV2ApiServiceDelegator<EDUCATION, EMPLOYMENT, EXTERNAL_IDENTIFIER, FUNDING, GROUP_ID_RECORD, OTHER_NAME, PEER_REVIEW, RESEARCHER_URL, WORK> {

    Response viewStatusText();

    Response viewActivities(String orcid);

    Response viewWork(String orcid, Long putCode);

    Response viewWorkSummary(String orcid, Long putCode);

    Response createWork(String orcid, WORK work);

    Response updateWork(String orcid, Long putCode, WORK work);

    Response deleteWork(String orcid, Long putCode);

    Response viewFunding(String orcid, Long putCode);

    Response viewFundingSummary(String orcid, Long putCode);

    Response createFunding(String orcid, FUNDING funding);

    Response updateFunding(String orcid, Long putCode, FUNDING funding);

    Response deleteFunding(String orcid, Long putCode);

    Response viewEducation(String orcid, Long putCode);

    Response viewEducationSummary(String orcid, Long putCode);

    Response createEducation(String orcid, EDUCATION education);

    Response updateEducation(String orcid, Long putCode, EDUCATION education);

    Response viewEmployment(String orcid, Long putCode);

    Response viewEmploymentSummary(String orcid, Long putCode);

    Response createEmployment(String orcid, EMPLOYMENT employment);

    Response updateEmployment(String orcid, Long putCode, EMPLOYMENT employment);

    Response deleteAffiliation(String orcid, Long putCode);

    Response viewPeerReview(String orcid, Long putCode);

    Response viewPeerReviewSummary(String orcid, Long putCode);

    Response createPeerReview(String orcid, PEER_REVIEW peerReview);

    Response updatePeerReview(String orcid, Long putCode, PEER_REVIEW peerReview);

    Response deletePeerReview(String orcid, Long putCode);

    Response viewGroupIdRecord(Long putCode);

    Response createGroupIdRecord(GROUP_ID_RECORD groupIdRecord);

    Response updateGroupIdRecord(GROUP_ID_RECORD groupIdRecord, Long putCode);

    Response deleteGroupIdRecord(Long putCode);

    Response viewGroupIdRecords(String pageSize, String pageNum);

    Response viewResearcherUrls(String orcid);

    Response viewResearcherUrl(String orcid, Long putCode);

    Response updateResearcherUrl(String orcid, Long putCode, RESEARCHER_URL researcherUrl);

    Response createResearcherUrl(String orcid, RESEARCHER_URL researcherUrl);

    Response deleteResearcherUrl(String orcid, Long putCode);

    Response viewEmails(String orcid);

    Response viewOtherNames(String orcid);

    Response viewOtherName(String orcid, Long putCode);

    Response createOtherName(String orcid, OTHER_NAME otherName);

    Response updateOtherName(String orcid, Long putCode, OTHER_NAME otherName);

    Response deleteOtherName(String orcid, Long putCode);

    Response viewPersonalDetails(String orcid);

    Response viewExternalIdentifiers(String orcid);

    Response viewExternalIdentifier(String orcid, Long putCode);

    Response updateExternalIdentifier(String orcid, Long putCode, EXTERNAL_IDENTIFIER externalIdentifier);

    Response createExternalIdentifier(String orcid, EXTERNAL_IDENTIFIER externalIdentifier);

    Response deleteExternalIdentifier(String orcid, Long putCode);
}
