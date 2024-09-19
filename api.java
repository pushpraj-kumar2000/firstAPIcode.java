public interface ICustomPropertyDetailsDAO extends IJdbcDaoSupport {
  Map<String,String>getPropertiesByPropertyCategoryPropertykey(String propertyCategory,String propertyKey)throws BusinessException;
  
}

public class CustomPropertyDetailsDAOImpl implements ICustomPropertyDetailsDAO {

	private static final IPOSLogger LOGGER = POSLoggerFactory.getLogger(CustomPropertyDetailsDAOImpl.class);

	@Override
	public Map<String, String> getPropertiesByPropertyCategoryPropertykey((String propertyCategory,String propertyKey) throws BusinessException {
		String propertyQuery = "select property_value from pos_property_details_custom where property_category=?& propertyKey=?";

		try {
			List<CustomPropertyDetails> customPropertyDetailsList = getJdbcTemplate().query(propertyQuery,
					(rs, rowNum) -> {
						CustomPropertyDetails customPropertyDetails = new CustomPropertyDetails();
						
						customPropertyDetails.setPropertyValue(rs.getString("property_value"));
						return customPropertyDetails;

					},propertyCategory,propertyKey);

			Map<String, String> propertyKVPair = new HashMap<>();

			for (CustomPropertyDetails details : customPropertyDetailsList) {
				propertyKVPair.put( details.getPropertyValue());
			}

			return propertyKVPair;

		} catch (DataAccessException exception) {
			LOGGER.error("Persistence Exception: " + exception.getMessage());
			throw new BusinessException(PersistenceExtensionMessageConstant.ERR_PE_001.getValue(),
					PersistenceExtensionMessageReader.getInstance(LocaleUtil.getLocale())
							.getString(PersistenceExtensionMessageConstant.ERR_PE_001.getCode()));
		}
	}

}


public class CustomPropertyDetails {
	
	private String propertyValue;

	public String getPropertyValue() {
		return propertyValue;
	}

	public void setPropertyValue(String propertyValue) {
		this.propertyValue = propertyValue;
	}

}





  public static final String CUSTOM_PROPERTY_DETAILS_DAO_BEAN_2="customPropertyDetailsDAOImpl";


public UIPropertyResponse getUIPropertyResponse(String propertyCategory, String propertyKey) {
		UIPropertyResponse uiPropertyResponse = new UIPropertyResponse();
		uiPropertyResponse.setArtsHeader(getNewARTSHeader());
		Map<String, String> property = new HashMap<>();
		try {
			if (StringUtils.isBlank(propertyCategory)) {
				return uiPropertyResponse;
			} else if (StringUtils.isNotBlank(propertyCategory) && StringUtils.isBlank(propertyKey)) {
				ICustomPropertyDetailsDAO customPropertyDetailsDAO = PersistenceExtensionServiceRegistry.getInstance()
						.getBean(PersistenceExtensionConstant.CUSTOM_PROPERTY_DETAILS_DAO_BEAN);
				property = customPropertyDetailsDAO.getPropertiesByPropertyCategory(propertyCategory);
			} else if (StringUtils.isNotBlank(propertyCategory) && StringUtils.isNotBlank(propertyKey)) {
				String propertyValue = getUIPropertyByCategory(propertyKey);
				ICustomPropertyDetailsDAO customPropertyDetailsDAO = PersistenceExtensionServiceRegistry.getInstance()
						.getBean(PersistenceExtensionConstant.CUSTOM_PROPERTY_DETAILS_DAO_BEAN);
				property = customPropertyDetailsDAO.getPropertiesByPropertyCategoryPropertyKey(propertyCategory,propertKey);
				if (StringUtils.isNotBlank(propertyValue)) {
					property.put(propertyKey, propertyValue);
				}
			}

			uiPropertyResponse.setProperty(property);

		} catch (BusinessException e) {
			uiPropertyResponse.getArtsHeader().setResponse(getErrorHeader(e.getErrorId(), e.getErrorMessage()));
		}

		return uiPropertyResponse;
	}
