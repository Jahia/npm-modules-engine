"use strict";
(self["webpackChunk_jahia_npm_module_example"] = self["webpackChunk_jahia_npm_module_example"] || []).push([["src_client_SampleI18n_jsx"],{

/***/ "./src/client/SampleI18n.jsx":
/*!***********************************!*\
  !*** ./src/client/SampleI18n.jsx ***!
  \***********************************/
/***/ ((__unused_webpack_module, __webpack_exports__, __webpack_require__) => {

__webpack_require__.r(__webpack_exports__);
/* harmony export */ __webpack_require__.d(__webpack_exports__, {
/* harmony export */   SampleI18n: () => (/* binding */ SampleI18n),
/* harmony export */   "default": () => (__WEBPACK_DEFAULT_EXPORT__)
/* harmony export */ });
/* harmony import */ var react__WEBPACK_IMPORTED_MODULE_0__ = __webpack_require__(/*! react */ "webpack/sharing/consume/default/react/react");
/* harmony import */ var react__WEBPACK_IMPORTED_MODULE_0___default = /*#__PURE__*/__webpack_require__.n(react__WEBPACK_IMPORTED_MODULE_0__);
/* harmony import */ var react_i18next__WEBPACK_IMPORTED_MODULE_1__ = __webpack_require__(/*! react-i18next */ "webpack/sharing/consume/default/react-i18next/react-i18next");
/* harmony import */ var react_i18next__WEBPACK_IMPORTED_MODULE_1___default = /*#__PURE__*/__webpack_require__.n(react_i18next__WEBPACK_IMPORTED_MODULE_1__);
function _slicedToArray(arr, i) { return _arrayWithHoles(arr) || _iterableToArrayLimit(arr, i) || _unsupportedIterableToArray(arr, i) || _nonIterableRest(); }
function _nonIterableRest() { throw new TypeError("Invalid attempt to destructure non-iterable instance.\nIn order to be iterable, non-array objects must have a [Symbol.iterator]() method."); }
function _unsupportedIterableToArray(o, minLen) { if (!o) return; if (typeof o === "string") return _arrayLikeToArray(o, minLen); var n = Object.prototype.toString.call(o).slice(8, -1); if (n === "Object" && o.constructor) n = o.constructor.name; if (n === "Map" || n === "Set") return Array.from(o); if (n === "Arguments" || /^(?:Ui|I)nt(?:8|16|32)(?:Clamped)?Array$/.test(n)) return _arrayLikeToArray(o, minLen); }
function _arrayLikeToArray(arr, len) { if (len == null || len > arr.length) len = arr.length; for (var i = 0, arr2 = new Array(len); i < len; i++) arr2[i] = arr[i]; return arr2; }
function _iterableToArrayLimit(arr, i) { var _i = null == arr ? null : "undefined" != typeof Symbol && arr[Symbol.iterator] || arr["@@iterator"]; if (null != _i) { var _s, _e, _x, _r, _arr = [], _n = !0, _d = !1; try { if (_x = (_i = _i.call(arr)).next, 0 === i) { if (Object(_i) !== _i) return; _n = !1; } else for (; !(_n = (_s = _x.call(_i)).done) && (_arr.push(_s.value), _arr.length !== i); _n = !0); } catch (err) { _d = !0, _e = err; } finally { try { if (!_n && null != _i.return && (_r = _i.return(), Object(_r) !== _r)) return; } finally { if (_d) throw _e; } } return _arr; } }
function _arrayWithHoles(arr) { if (Array.isArray(arr)) return arr; }


var SampleI18n = function SampleI18n(_ref) {
  var _ref$placeholder = _ref.placeholder,
    placeholder = _ref$placeholder === void 0 ? '' : _ref$placeholder;
  var _useTranslation = (0,react_i18next__WEBPACK_IMPORTED_MODULE_1__.useTranslation)(),
    t = _useTranslation.t;
  var _useState = (0,react__WEBPACK_IMPORTED_MODULE_0__.useState)(placeholder),
    _useState2 = _slicedToArray(_useState, 2),
    updatedPlaceholder = _useState2[0],
    setUpdatedPlaceholder = _useState2[1];
  return /*#__PURE__*/react__WEBPACK_IMPORTED_MODULE_0___default().createElement((react__WEBPACK_IMPORTED_MODULE_0___default().Fragment), null, /*#__PURE__*/react__WEBPACK_IMPORTED_MODULE_0___default().createElement("h3", null, "Test i18n"), /*#__PURE__*/react__WEBPACK_IMPORTED_MODULE_0___default().createElement("div", {
    "data-testid": "i18n-simple"
  }, t('greeting')), /*#__PURE__*/react__WEBPACK_IMPORTED_MODULE_0___default().createElement("div", {
    "data-testid": "i18n-placeholder"
  }, t('test.composed', {
    placeholder: updatedPlaceholder
  })), /*#__PURE__*/react__WEBPACK_IMPORTED_MODULE_0___default().createElement("p", null, /*#__PURE__*/react__WEBPACK_IMPORTED_MODULE_0___default().createElement("span", null, "Edit to update i18n placeholder:"), /*#__PURE__*/react__WEBPACK_IMPORTED_MODULE_0___default().createElement("input", {
    "data-testid": "i18n-edit-placeholder",
    type: "text",
    value: updatedPlaceholder,
    onChange: function onChange(event) {
      setUpdatedPlaceholder(event.target.value);
    }
  })));
};
/* harmony default export */ const __WEBPACK_DEFAULT_EXPORT__ = (SampleI18n);

/***/ })

}]);
//# sourceMappingURL=data:application/json;charset=utf-8;base64,eyJ2ZXJzaW9uIjozLCJmaWxlIjoic3JjX2NsaWVudF9TYW1wbGVJMThuX2pzeC5qcyIsIm1hcHBpbmdzIjoiOzs7Ozs7Ozs7Ozs7Ozs7Ozs7Ozs7Ozs7QUFBd0M7QUFDSztBQUV0QyxJQUFNRyxVQUFVLEdBQUcsU0FBYkEsVUFBVUEsQ0FBQUMsSUFBQSxFQUEyQjtFQUFBLElBQUFDLGdCQUFBLEdBQUFELElBQUEsQ0FBdEJFLFdBQVc7SUFBWEEsV0FBVyxHQUFBRCxnQkFBQSxjQUFHLEVBQUUsR0FBQUEsZ0JBQUE7RUFDeEMsSUFBQUUsZUFBQSxHQUFjTCw2REFBYyxDQUFDLENBQUM7SUFBdEJNLENBQUMsR0FBQUQsZUFBQSxDQUFEQyxDQUFDO0VBQ1QsSUFBQUMsU0FBQSxHQUFvRFIsK0NBQVEsQ0FBQ0ssV0FBVyxDQUFDO0lBQUFJLFVBQUEsR0FBQUMsY0FBQSxDQUFBRixTQUFBO0lBQWxFRyxrQkFBa0IsR0FBQUYsVUFBQTtJQUFFRyxxQkFBcUIsR0FBQUgsVUFBQTtFQUVoRCxvQkFDSVYsMERBQUEsQ0FBQUEsdURBQUEscUJBQ0lBLDBEQUFBLGFBQUksV0FBYSxDQUFDLGVBQ2xCQSwwREFBQTtJQUFLLGVBQVk7RUFBYSxHQUFFUSxDQUFDLENBQUMsVUFBVSxDQUFPLENBQUMsZUFDcERSLDBEQUFBO0lBQUssZUFBWTtFQUFrQixHQUFFUSxDQUFDLENBQUMsZUFBZSxFQUFFO0lBQUVGLFdBQVcsRUFBRU07RUFBbUIsQ0FBQyxDQUFPLENBQUMsZUFDbkdaLDBEQUFBLHlCQUNJQSwwREFBQSxlQUFNLGtDQUFzQyxDQUFDLGVBQzdDQSwwREFBQTtJQUNJLGVBQVksdUJBQXVCO0lBQ25DZ0IsSUFBSSxFQUFDLE1BQU07SUFDWEMsS0FBSyxFQUFFTCxrQkFBbUI7SUFDMUJNLFFBQVEsRUFBRSxTQUFBQSxTQUFDQyxLQUFLLEVBQUs7TUFDakJOLHFCQUFxQixDQUFDTSxLQUFLLENBQUNDLE1BQU0sQ0FBQ0gsS0FBSyxDQUFDO0lBQzdDO0VBQUUsQ0FDTCxDQUNGLENBQ0wsQ0FBQztBQUVYLENBQUM7QUFFRCxpRUFBZWQsVUFBVSIsInNvdXJjZXMiOlsid2VicGFjazovL0BqYWhpYS9ucG0tbW9kdWxlLWV4YW1wbGUvLi9zcmMvY2xpZW50L1NhbXBsZUkxOG4uanN4Il0sInNvdXJjZXNDb250ZW50IjpbImltcG9ydCBSZWFjdCwgeyB1c2VTdGF0ZSB9IGZyb20gJ3JlYWN0JztcbmltcG9ydCB7dXNlVHJhbnNsYXRpb259IGZyb20gXCJyZWFjdC1pMThuZXh0XCI7XG5cbmV4cG9ydCBjb25zdCBTYW1wbGVJMThuID0gKHtwbGFjZWhvbGRlciA9ICcnfSkgPT4ge1xuICAgIGNvbnN0IHsgdCB9ID0gdXNlVHJhbnNsYXRpb24oKTtcbiAgICBjb25zdCBbdXBkYXRlZFBsYWNlaG9sZGVyLCBzZXRVcGRhdGVkUGxhY2Vob2xkZXJdID0gdXNlU3RhdGUocGxhY2Vob2xkZXIpO1xuXG4gICAgcmV0dXJuIChcbiAgICAgICAgPD5cbiAgICAgICAgICAgIDxoMz5UZXN0IGkxOG48L2gzPlxuICAgICAgICAgICAgPGRpdiBkYXRhLXRlc3RpZD1cImkxOG4tc2ltcGxlXCI+e3QoJ2dyZWV0aW5nJyl9PC9kaXY+XG4gICAgICAgICAgICA8ZGl2IGRhdGEtdGVzdGlkPVwiaTE4bi1wbGFjZWhvbGRlclwiPnt0KCd0ZXN0LmNvbXBvc2VkJywgeyBwbGFjZWhvbGRlcjogdXBkYXRlZFBsYWNlaG9sZGVyIH0pfTwvZGl2PlxuICAgICAgICAgICAgPHA+XG4gICAgICAgICAgICAgICAgPHNwYW4+RWRpdCB0byB1cGRhdGUgaTE4biBwbGFjZWhvbGRlcjo8L3NwYW4+XG4gICAgICAgICAgICAgICAgPGlucHV0XG4gICAgICAgICAgICAgICAgICAgIGRhdGEtdGVzdGlkPVwiaTE4bi1lZGl0LXBsYWNlaG9sZGVyXCJcbiAgICAgICAgICAgICAgICAgICAgdHlwZT1cInRleHRcIlxuICAgICAgICAgICAgICAgICAgICB2YWx1ZT17dXBkYXRlZFBsYWNlaG9sZGVyfVxuICAgICAgICAgICAgICAgICAgICBvbkNoYW5nZT17KGV2ZW50KSA9PiB7XG4gICAgICAgICAgICAgICAgICAgICAgICBzZXRVcGRhdGVkUGxhY2Vob2xkZXIoZXZlbnQudGFyZ2V0LnZhbHVlKTtcbiAgICAgICAgICAgICAgICAgICAgfX1cbiAgICAgICAgICAgICAgICAvPlxuICAgICAgICAgICAgPC9wPlxuICAgICAgICA8Lz5cbiAgICApO1xufVxuXG5leHBvcnQgZGVmYXVsdCBTYW1wbGVJMThuOyJdLCJuYW1lcyI6WyJSZWFjdCIsInVzZVN0YXRlIiwidXNlVHJhbnNsYXRpb24iLCJTYW1wbGVJMThuIiwiX3JlZiIsIl9yZWYkcGxhY2Vob2xkZXIiLCJwbGFjZWhvbGRlciIsIl91c2VUcmFuc2xhdGlvbiIsInQiLCJfdXNlU3RhdGUiLCJfdXNlU3RhdGUyIiwiX3NsaWNlZFRvQXJyYXkiLCJ1cGRhdGVkUGxhY2Vob2xkZXIiLCJzZXRVcGRhdGVkUGxhY2Vob2xkZXIiLCJjcmVhdGVFbGVtZW50IiwiRnJhZ21lbnQiLCJ0eXBlIiwidmFsdWUiLCJvbkNoYW5nZSIsImV2ZW50IiwidGFyZ2V0Il0sInNvdXJjZVJvb3QiOiIifQ==