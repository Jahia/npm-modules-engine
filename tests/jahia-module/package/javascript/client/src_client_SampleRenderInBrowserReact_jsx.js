"use strict";
(self["webpackChunk_jahia_npm_module_example"] = self["webpackChunk_jahia_npm_module_example"] || []).push([["src_client_SampleRenderInBrowserReact_jsx"],{

/***/ "./src/client/SampleRenderInBrowserReact.jsx":
/*!***************************************************!*\
  !*** ./src/client/SampleRenderInBrowserReact.jsx ***!
  \***************************************************/
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

var SampleRenderInBrowserReact = function SampleRenderInBrowserReact(_ref) {
  var path = _ref.path;
  var _useState = (0,react__WEBPACK_IMPORTED_MODULE_0__.useState)(new Date()),
    _useState2 = _slicedToArray(_useState, 2),
    currentDate = _useState2[0],
    setCurrentDate = _useState2[1];
  var _useState3 = (0,react__WEBPACK_IMPORTED_MODULE_0__.useState)(3),
    _useState4 = _slicedToArray(_useState3, 2),
    counter = _useState4[0],
    setCounter = _useState4[1];
  var updateDate = function updateDate() {
    setCurrentDate(new Date());
  };
  (0,react__WEBPACK_IMPORTED_MODULE_0__.useEffect)(function () {
    counter > 0 && setTimeout(function () {
      return setCounter(counter - 1);
    }, 1000);
    var timerID = setInterval(updateDate, 2000);
    return function () {
      return clearInterval(timerID);
    };
  }, [currentDate, counter]);
  return /*#__PURE__*/react__WEBPACK_IMPORTED_MODULE_0___default().createElement("div", null, /*#__PURE__*/react__WEBPACK_IMPORTED_MODULE_0___default().createElement("h2", null, "This React component is fully rendered client side:"), /*#__PURE__*/react__WEBPACK_IMPORTED_MODULE_0___default().createElement("p", null, "Able to display current node path: ", /*#__PURE__*/react__WEBPACK_IMPORTED_MODULE_0___default().createElement("span", {
    "data-testid": "path"
  }, path)), /*#__PURE__*/react__WEBPACK_IMPORTED_MODULE_0___default().createElement("p", null, "And refreshing date every 2 sec: ", /*#__PURE__*/react__WEBPACK_IMPORTED_MODULE_0___default().createElement("span", {
    "data-testid": "date"
  }, currentDate.toLocaleString())), /*#__PURE__*/react__WEBPACK_IMPORTED_MODULE_0___default().createElement("p", null, "Countdown: ", /*#__PURE__*/react__WEBPACK_IMPORTED_MODULE_0___default().createElement("span", {
    "data-testid": "counter"
  }, counter)));
};
/* harmony default export */ const __WEBPACK_DEFAULT_EXPORT__ = (SampleRenderInBrowserReact);

/***/ })

}]);
//# sourceMappingURL=data:application/json;charset=utf-8;base64,eyJ2ZXJzaW9uIjozLCJmaWxlIjoic3JjX2NsaWVudF9TYW1wbGVSZW5kZXJJbkJyb3dzZXJSZWFjdF9qc3guanMiLCJtYXBwaW5ncyI6Ijs7Ozs7Ozs7Ozs7Ozs7Ozs7Ozs7O0FBQWlEO0FBRWpELElBQU1HLDBCQUEwQixHQUFHLFNBQTdCQSwwQkFBMEJBLENBQUFDLElBQUEsRUFBZTtFQUFBLElBQVZDLElBQUksR0FBQUQsSUFBQSxDQUFKQyxJQUFJO0VBQ3JDLElBQUFDLFNBQUEsR0FBc0NKLCtDQUFRLENBQUMsSUFBSUssSUFBSSxDQUFDLENBQUMsQ0FBQztJQUFBQyxVQUFBLEdBQUFDLGNBQUEsQ0FBQUgsU0FBQTtJQUFuREksV0FBVyxHQUFBRixVQUFBO0lBQUVHLGNBQWMsR0FBQUgsVUFBQTtFQUNsQyxJQUFBSSxVQUFBLEdBQThCViwrQ0FBUSxDQUFDLENBQUMsQ0FBQztJQUFBVyxVQUFBLEdBQUFKLGNBQUEsQ0FBQUcsVUFBQTtJQUFsQ0UsT0FBTyxHQUFBRCxVQUFBO0lBQUVFLFVBQVUsR0FBQUYsVUFBQTtFQUUxQixJQUFNRyxVQUFVLEdBQUcsU0FBYkEsVUFBVUEsQ0FBQSxFQUFTO0lBQ3JCTCxjQUFjLENBQUMsSUFBSUosSUFBSSxDQUFDLENBQUMsQ0FBQztFQUM5QixDQUFDO0VBRUROLGdEQUFTLENBQUMsWUFBTTtJQUNaYSxPQUFPLEdBQUcsQ0FBQyxJQUFJRyxVQUFVLENBQUM7TUFBQSxPQUFNRixVQUFVLENBQUNELE9BQU8sR0FBRyxDQUFDLENBQUM7SUFBQSxHQUFFLElBQUksQ0FBQztJQUM5RCxJQUFNSSxPQUFPLEdBQUdDLFdBQVcsQ0FBQ0gsVUFBVSxFQUFFLElBQUksQ0FBQztJQUM3QyxPQUFPO01BQUEsT0FBTUksYUFBYSxDQUFDRixPQUFPLENBQUM7SUFBQTtFQUN2QyxDQUFDLEVBQUUsQ0FBQ1IsV0FBVyxFQUFFSSxPQUFPLENBQUMsQ0FBQztFQUUxQixvQkFDSWQsMERBQUEsMkJBQ0lBLDBEQUFBLGFBQUkscURBQXVELENBQUMsZUFDNURBLDBEQUFBLFlBQUcscUNBQW1DLGVBQUFBLDBEQUFBO0lBQU0sZUFBWTtFQUFNLEdBQUVLLElBQVcsQ0FBSSxDQUFDLGVBQ2hGTCwwREFBQSxZQUFHLG1DQUFpQyxlQUFBQSwwREFBQTtJQUFNLGVBQVk7RUFBTSxHQUFFVSxXQUFXLENBQUNZLGNBQWMsQ0FBQyxDQUFRLENBQUksQ0FBQyxlQUN0R3RCLDBEQUFBLFlBQUcsYUFBVyxlQUFBQSwwREFBQTtJQUFNLGVBQVk7RUFBUyxHQUFFYyxPQUFjLENBQUksQ0FDNUQsQ0FBQztBQUVkLENBQUM7QUFFRCxpRUFBZVgsMEJBQTBCIiwic291cmNlcyI6WyJ3ZWJwYWNrOi8vQGphaGlhL25wbS1tb2R1bGUtZXhhbXBsZS8uL3NyYy9jbGllbnQvU2FtcGxlUmVuZGVySW5Ccm93c2VyUmVhY3QuanN4Il0sInNvdXJjZXNDb250ZW50IjpbImltcG9ydCBSZWFjdCwge3VzZUVmZmVjdCwgdXNlU3RhdGV9IGZyb20gJ3JlYWN0JztcblxuY29uc3QgU2FtcGxlUmVuZGVySW5Ccm93c2VyUmVhY3QgPSAoe3BhdGh9KSA9PiB7XG4gICAgY29uc3QgW2N1cnJlbnREYXRlLCBzZXRDdXJyZW50RGF0ZV0gPSB1c2VTdGF0ZShuZXcgRGF0ZSgpKTtcbiAgICBjb25zdCBbY291bnRlciwgc2V0Q291bnRlcl0gPSB1c2VTdGF0ZSgzKTtcblxuICAgIGNvbnN0IHVwZGF0ZURhdGUgPSAoKSA9PiB7XG4gICAgICAgIHNldEN1cnJlbnREYXRlKG5ldyBEYXRlKCkpO1xuICAgIH07XG5cbiAgICB1c2VFZmZlY3QoKCkgPT4ge1xuICAgICAgICBjb3VudGVyID4gMCAmJiBzZXRUaW1lb3V0KCgpID0+IHNldENvdW50ZXIoY291bnRlciAtIDEpLCAxMDAwKTtcbiAgICAgICAgY29uc3QgdGltZXJJRCA9IHNldEludGVydmFsKHVwZGF0ZURhdGUsIDIwMDApO1xuICAgICAgICByZXR1cm4gKCkgPT4gY2xlYXJJbnRlcnZhbCh0aW1lcklEKTtcbiAgICB9LCBbY3VycmVudERhdGUsIGNvdW50ZXJdKTtcblxuICAgIHJldHVybiAoXG4gICAgICAgIDxkaXY+XG4gICAgICAgICAgICA8aDI+VGhpcyBSZWFjdCBjb21wb25lbnQgaXMgZnVsbHkgcmVuZGVyZWQgY2xpZW50IHNpZGU6PC9oMj5cbiAgICAgICAgICAgIDxwPkFibGUgdG8gZGlzcGxheSBjdXJyZW50IG5vZGUgcGF0aDogPHNwYW4gZGF0YS10ZXN0aWQ9XCJwYXRoXCI+e3BhdGh9PC9zcGFuPjwvcD5cbiAgICAgICAgICAgIDxwPkFuZCByZWZyZXNoaW5nIGRhdGUgZXZlcnkgMiBzZWM6IDxzcGFuIGRhdGEtdGVzdGlkPVwiZGF0ZVwiPntjdXJyZW50RGF0ZS50b0xvY2FsZVN0cmluZygpfTwvc3Bhbj48L3A+XG4gICAgICAgICAgICA8cD5Db3VudGRvd246IDxzcGFuIGRhdGEtdGVzdGlkPVwiY291bnRlclwiPntjb3VudGVyfTwvc3Bhbj48L3A+XG4gICAgICAgIDwvZGl2PlxuICAgICk7XG59XG5cbmV4cG9ydCBkZWZhdWx0IFNhbXBsZVJlbmRlckluQnJvd3NlclJlYWN0OyJdLCJuYW1lcyI6WyJSZWFjdCIsInVzZUVmZmVjdCIsInVzZVN0YXRlIiwiU2FtcGxlUmVuZGVySW5Ccm93c2VyUmVhY3QiLCJfcmVmIiwicGF0aCIsIl91c2VTdGF0ZSIsIkRhdGUiLCJfdXNlU3RhdGUyIiwiX3NsaWNlZFRvQXJyYXkiLCJjdXJyZW50RGF0ZSIsInNldEN1cnJlbnREYXRlIiwiX3VzZVN0YXRlMyIsIl91c2VTdGF0ZTQiLCJjb3VudGVyIiwic2V0Q291bnRlciIsInVwZGF0ZURhdGUiLCJzZXRUaW1lb3V0IiwidGltZXJJRCIsInNldEludGVydmFsIiwiY2xlYXJJbnRlcnZhbCIsImNyZWF0ZUVsZW1lbnQiLCJ0b0xvY2FsZVN0cmluZyJdLCJzb3VyY2VSb290IjoiIn0=