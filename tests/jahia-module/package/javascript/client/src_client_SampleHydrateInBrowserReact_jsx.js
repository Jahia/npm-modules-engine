"use strict";
(self["webpackChunk_jahia_npm_module_example"] = self["webpackChunk_jahia_npm_module_example"] || []).push([["src_client_SampleHydrateInBrowserReact_jsx"],{

/***/ "./src/client/SampleHydrateInBrowserReact.jsx":
/*!****************************************************!*\
  !*** ./src/client/SampleHydrateInBrowserReact.jsx ***!
  \****************************************************/
/***/ ((__unused_webpack_module, __webpack_exports__, __webpack_require__) => {

__webpack_require__.r(__webpack_exports__);
/* harmony export */ __webpack_require__.d(__webpack_exports__, {
/* harmony export */   "default": () => (__WEBPACK_DEFAULT_EXPORT__)
/* harmony export */ });
/* harmony import */ var react__WEBPACK_IMPORTED_MODULE_0__ = __webpack_require__(/*! react */ "webpack/sharing/consume/default/react/react");
/* harmony import */ var react__WEBPACK_IMPORTED_MODULE_0___default = /*#__PURE__*/__webpack_require__.n(react__WEBPACK_IMPORTED_MODULE_0__);
function _slicedToArray(arr, i) { return _arrayWithHoles(arr) || _iterableToArrayLimit(arr, i) || _unsupportedIterableToArray(arr, i) || _nonIterableRest(); }
function _nonIterableRest() { throw new TypeError("Invalid attempt to destructure non-iterable instance.\nIn order to be iterable, non-array objects must have a [Symbol.iterator]() method."); }
function _unsupportedIterableToArray(o, minLen) { if (!o) return; if (typeof o === "string") return _arrayLikeToArray(o, minLen); var n = Object.prototype.toString.call(o).slice(8, -1); if (n === "Object" && o.constructor) n = o.constructor.name; if (n === "Map" || n === "Set") return Array.from(o); if (n === "Arguments" || /^(?:Ui|I)nt(?:8|16|32)(?:Clamped)?Array$/.test(n)) return _arrayLikeToArray(o, minLen); }
function _arrayLikeToArray(arr, len) { if (len == null || len > arr.length) len = arr.length; for (var i = 0, arr2 = new Array(len); i < len; i++) arr2[i] = arr[i]; return arr2; }
function _iterableToArrayLimit(arr, i) { var _i = null == arr ? null : "undefined" != typeof Symbol && arr[Symbol.iterator] || arr["@@iterator"]; if (null != _i) { var _s, _e, _x, _r, _arr = [], _n = !0, _d = !1; try { if (_x = (_i = _i.call(arr)).next, 0 === i) { if (Object(_i) !== _i) return; _n = !1; } else for (; !(_n = (_s = _x.call(_i)).done) && (_arr.push(_s.value), _arr.length !== i); _n = !0); } catch (err) { _d = !0, _e = err; } finally { try { if (!_n && null != _i.return && (_r = _i.return(), Object(_r) !== _r)) return; } finally { if (_d) throw _e; } } return _arr; } }
function _arrayWithHoles(arr) { if (Array.isArray(arr)) return arr; }

var SampleHydrateInBrowserReact = function SampleHydrateInBrowserReact(_ref) {
  var initialValue = _ref.initialValue;
  var _useState = (0,react__WEBPACK_IMPORTED_MODULE_0__.useState)(initialValue),
    _useState2 = _slicedToArray(_useState, 2),
    count = _useState2[0],
    setCount = _useState2[1];
  var handleClick = function handleClick() {
    setCount(count + 1);
  };
  return /*#__PURE__*/react__WEBPACK_IMPORTED_MODULE_0___default().createElement("div", null, /*#__PURE__*/react__WEBPACK_IMPORTED_MODULE_0___default().createElement("h2", null, "This React component is hydrated client side:"), /*#__PURE__*/react__WEBPACK_IMPORTED_MODULE_0___default().createElement("p", {
    "data-testid": "count"
  }, "Count: ", count), /*#__PURE__*/react__WEBPACK_IMPORTED_MODULE_0___default().createElement("button", {
    "data-testid": "count-button",
    onClick: handleClick
  }, "Increment"));
};
/* harmony default export */ const __WEBPACK_DEFAULT_EXPORT__ = (SampleHydrateInBrowserReact);

/***/ })

}]);
//# sourceMappingURL=data:application/json;charset=utf-8;base64,eyJ2ZXJzaW9uIjozLCJmaWxlIjoic3JjX2NsaWVudF9TYW1wbGVIeWRyYXRlSW5Ccm93c2VyUmVhY3RfanN4LmpzIiwibWFwcGluZ3MiOiI7Ozs7Ozs7Ozs7Ozs7Ozs7Ozs7OztBQUF3QztBQUV4QyxJQUFNRSwyQkFBMkIsR0FBRyxTQUE5QkEsMkJBQTJCQSxDQUFBQyxJQUFBLEVBQXVCO0VBQUEsSUFBbEJDLFlBQVksR0FBQUQsSUFBQSxDQUFaQyxZQUFZO0VBQzlDLElBQUFDLFNBQUEsR0FBMEJKLCtDQUFRLENBQUNHLFlBQVksQ0FBQztJQUFBRSxVQUFBLEdBQUFDLGNBQUEsQ0FBQUYsU0FBQTtJQUF6Q0csS0FBSyxHQUFBRixVQUFBO0lBQUVHLFFBQVEsR0FBQUgsVUFBQTtFQUV0QixJQUFNSSxXQUFXLEdBQUcsU0FBZEEsV0FBV0EsQ0FBQSxFQUFTO0lBQ3RCRCxRQUFRLENBQUNELEtBQUssR0FBRyxDQUFDLENBQUM7RUFDdkIsQ0FBQztFQUVELG9CQUNJUiwwREFBQSwyQkFDSUEsMERBQUEsYUFBSSwrQ0FBaUQsQ0FBQyxlQUN0REEsMERBQUE7SUFBRyxlQUFZO0VBQU8sR0FBQyxTQUFPLEVBQUNRLEtBQVMsQ0FBQyxlQUN6Q1IsMERBQUE7SUFBUSxlQUFZLGNBQWM7SUFBQ1ksT0FBTyxFQUFFRjtFQUFZLEdBQUMsV0FBaUIsQ0FDekUsQ0FBQztBQUVkLENBQUM7QUFFRCxpRUFBZVIsMkJBQTJCIiwic291cmNlcyI6WyJ3ZWJwYWNrOi8vQGphaGlhL25wbS1tb2R1bGUtZXhhbXBsZS8uL3NyYy9jbGllbnQvU2FtcGxlSHlkcmF0ZUluQnJvd3NlclJlYWN0LmpzeCJdLCJzb3VyY2VzQ29udGVudCI6WyJpbXBvcnQgUmVhY3QsIHsgdXNlU3RhdGUgfSBmcm9tICdyZWFjdCc7XG5cbmNvbnN0IFNhbXBsZUh5ZHJhdGVJbkJyb3dzZXJSZWFjdCA9ICh7aW5pdGlhbFZhbHVlfSkgPT4ge1xuICAgIGNvbnN0IFtjb3VudCwgc2V0Q291bnRdID0gdXNlU3RhdGUoaW5pdGlhbFZhbHVlKTtcblxuICAgIGNvbnN0IGhhbmRsZUNsaWNrID0gKCkgPT4ge1xuICAgICAgICBzZXRDb3VudChjb3VudCArIDEpO1xuICAgIH07XG5cbiAgICByZXR1cm4gKFxuICAgICAgICA8ZGl2PlxuICAgICAgICAgICAgPGgyPlRoaXMgUmVhY3QgY29tcG9uZW50IGlzIGh5ZHJhdGVkIGNsaWVudCBzaWRlOjwvaDI+XG4gICAgICAgICAgICA8cCBkYXRhLXRlc3RpZD1cImNvdW50XCI+Q291bnQ6IHtjb3VudH08L3A+XG4gICAgICAgICAgICA8YnV0dG9uIGRhdGEtdGVzdGlkPVwiY291bnQtYnV0dG9uXCIgb25DbGljaz17aGFuZGxlQ2xpY2t9PkluY3JlbWVudDwvYnV0dG9uPlxuICAgICAgICA8L2Rpdj5cbiAgICApO1xufVxuXG5leHBvcnQgZGVmYXVsdCBTYW1wbGVIeWRyYXRlSW5Ccm93c2VyUmVhY3Q7Il0sIm5hbWVzIjpbIlJlYWN0IiwidXNlU3RhdGUiLCJTYW1wbGVIeWRyYXRlSW5Ccm93c2VyUmVhY3QiLCJfcmVmIiwiaW5pdGlhbFZhbHVlIiwiX3VzZVN0YXRlIiwiX3VzZVN0YXRlMiIsIl9zbGljZWRUb0FycmF5IiwiY291bnQiLCJzZXRDb3VudCIsImhhbmRsZUNsaWNrIiwiY3JlYXRlRWxlbWVudCIsIm9uQ2xpY2siXSwic291cmNlUm9vdCI6IiJ9