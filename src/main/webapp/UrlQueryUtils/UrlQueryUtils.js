/** This class is responsible for handling url query parameters. */
class UrlQueryUtils {
  static getParamsFromQueryString() {
    const GET = {};
    const queryString = decodeURI(window.location.search.replace(/^\?/, ''));
    queryString.split(/\&/).forEach(function(keyValuePair) {
      const paramName = keyValuePair.replace(/=.*$/, '');
      const paramValue = keyValuePair.replace(/^[^=]*\=/, '');
      GET[paramName] = paramValue;
    });
    return GET;
  }
}
