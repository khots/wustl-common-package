/*L
  Copyright Washington University in St. Louis, SemanticBits, Persistent Systems, Krishagni.

  Distributed under the OSI-approved BSD 3-Clause License.
  See http://ncip.github.com/wustl-common-package/LICENSE.txt for details.
L*/

alter table CATISSUE_SEARCH_DISPLAY_DATA ADD COLUMN DEFAULT_VIEW_ATTRIBUTE bit DEFAULT;
update CATISSUE_SEARCH_DISPLAY_DATA set COLUMN DEFAULT_VIEW_ATTRIBUTE=1;