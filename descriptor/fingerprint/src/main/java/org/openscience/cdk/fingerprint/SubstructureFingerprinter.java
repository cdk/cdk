/* Copyright (C) 2005-2007  The Chemistry Development Kit (CDK) project
 *
 * Contact: cdk-devel@lists.sourceforge.net
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1
 * of the License, or (at your option) any later version.
 * All we ask is that proper credit is given for our work, which includes
 * - but is not limited to - adding the above copyright notice to the beginning
 * of your source code files, and to any copyright notice that you may distribute
 * with programs based on this work.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 */
package org.openscience.cdk.fingerprint;

import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.isomorphism.Pattern;
import org.openscience.cdk.isomorphism.matchers.QueryAtomContainer;
import org.openscience.cdk.smarts.SmartsPattern;

import java.util.*;

/**
 * {@link IFingerprinter} that gives a bit set which has a size equal to the number
 * of substructures it was constructed from. A set bit indicates that that
 * substructure was found at least once in the molecule for which the
 * fingerprint was calculated. The fingerprint currently supports 307
 * substructures, listed below:
 *

 * <table>
 * <caption>Table: 1: Default substructure patterns (SMARTS) for each bit position</caption>
 * <thead>
 * <tr>
 * <td>Bit position</td><td>Description</td><td>Pattern</td>
 * </tr>
 * </thead>
 * <tbody>
 * <tr><td>0</td><td>Primary carbon</td><td>{@code [CX4H3][#6]}</td></tr>
 * <tr><td>1</td><td>Secondary carbon</td><td>{@code [CX4H2]([#6])[#6]}</td></tr>
 * <tr><td>2</td><td>Tertiary carbon</td><td>{@code [CX4H1]([#6])([#6])[#6]}</td></tr>
 * <tr><td>3</td><td>Quaternary carbon</td><td>{@code [CX4]([#6])([#6])([#6])[#6]}</td></tr>
 * <tr><td>4</td><td>Alkene</td><td>{@code [CX3;$([H2]),$([H1][#6]),$(C([#6])[#6])]=[CX3;$([H2]),$([H1][#6]),$(C([#6])[#6])]}</td></tr>
 * <tr><td>5</td><td>Alkyne</td><td>{@code [CX2]#[CX2]}</td></tr>
 * <tr><td>6</td><td>Allene</td><td>{@code [CX3]=[CX2]=[CX3]}</td></tr>
 * <tr><td>7</td><td>Alkylchloride</td><td>{@code [ClX1][CX4]}</td></tr>
 * <tr><td>8</td><td>Alkylfluoride</td><td>{@code [FX1][CX4]}</td></tr>
 * <tr><td>9</td><td>Alkylbromide</td><td>{@code [BrX1][CX4]}</td></tr>
 * <tr><td>10</td><td>Alkyliodide</td><td>{@code [IX1][CX4]}</td></tr>
 * <tr><td>11</td><td>Alcohol</td><td>{@code [OX2H][CX4;!$(C([OX2H])[O,S,#7,#15])]}</td></tr>
 * <tr><td>12</td><td>Primary alcohol</td><td>{@code [OX2H][CX4H2;!$(C([OX2H])[O,S,#7,#15])]}</td></tr>
 * <tr><td>13</td><td>Secondary alcohol</td><td>{@code [OX2H][CX4H;!$(C([OX2H])[O,S,#7,#15])]}</td></tr>
 * <tr><td>14</td><td>Tertiary alcohol</td><td>{@code [OX2H][CX4D4;!$(C([OX2H])[O,S,#7,#15])]}</td></tr>
 * <tr><td>15</td><td>Dialkylether</td><td>{@code [OX2]([CX4;!$(C([OX2])[O,S,#7,#15,F,Cl,Br,I])])[CX4;!$(C([OX2])[O,S,#7,#15])]}</td></tr>
 * <tr><td>16</td><td>Dialkylthioether</td><td>{@code [SX2]([CX4;!$(C([OX2])[O,S,#7,#15,F,Cl,Br,I])])[CX4;!$(C([OX2])[O,S,#7,#15])]}</td></tr>
 * <tr><td>17</td><td>Alkylarylether</td><td>{@code [OX2](c)[CX4;!$(C([OX2])[O,S,#7,#15,F,Cl,Br,I])]}</td></tr>
 * <tr><td>18</td><td>Diarylether</td><td>{@code [c][OX2][c]}</td></tr>
 * <tr><td>19</td><td>Alkylarylthioether</td><td>{@code [SX2](c)[CX4;!$(C([OX2])[O,S,#7,#15,F,Cl,Br,I])]}</td></tr>
 * <tr><td>20</td><td>Diarylthioether</td><td>{@code [c][SX2][c]}</td></tr>
 * <tr><td>21</td><td>Oxonium</td><td>{@code [O+;!$([O]~[!#6]);!$([S]*~[#7,#8,#15,#16])]}</td></tr>
 * <tr><td>22</td><td>Amine</td><td>{@code [NX3+0,NX4+;!$([N]~[!#6]);!$([N]*~[#7,#8,#15,#16])]}</td></tr>
 * <tr><td>23</td><td>Primary aliph amine</td><td>{@code [NX3H2+0,NX4H3+;!$([N][!C]);!$([N]*~[#7,#8,#15,#16])]}</td></tr>
 * <tr><td>24</td><td>Secondary aliph amine</td><td>{@code [NX3H1+0,NX4H2+;!$([N][!C]);!$([N]*~[#7,#8,#15,#16])]}</td></tr>
 * <tr><td>25</td><td>Tertiary aliph amine</td><td>{@code [NX3H0+0,NX4H1+;!$([N][!C]);!$([N]*~[#7,#8,#15,#16])]}</td></tr>
 * <tr><td>26</td><td>Quaternary aliph ammonium</td><td>{@code [NX4H0+;!$([N][!C]);!$([N]*~[#7,#8,#15,#16])]}</td></tr>
 * <tr><td>27</td><td>Primary arom amine</td><td>{@code [NX3H2+0,NX4H3+]c}</td></tr>
 * <tr><td>28</td><td>Secondary arom amine</td><td>{@code [NX3H1+0,NX4H2+;!$([N][!c]);!$([N]*~[#7,#8,#15,#16])]}</td></tr>
 * <tr><td>29</td><td>Tertiary arom amine</td><td>{@code [NX3H0+0,NX4H1+;!$([N][!c]);!$([N]*~[#7,#8,#15,#16])]}</td></tr>
 * <tr><td>30</td><td>Quaternary arom ammonium</td><td>{@code [NX4H0+;!$([N][!c]);!$([N]*~[#7,#8,#15,#16])]}</td></tr>
 * <tr><td>31</td><td>Secondary mixed amine</td><td>{@code [NX3H1+0,NX4H2+;$([N]([c])[C]);!$([N]*~[#7,#8,#15,#16])]}</td></tr>
 * <tr><td>32</td><td>Tertiary mixed amine</td><td>{@code [NX3H0+0,NX4H1+;$([N]([c])([C])[#6]);!$([N]*~[#7,#8,#15,#16])]}</td></tr>
 * <tr><td>33</td><td>Quaternary mixed ammonium</td><td>{@code [NX4H0+;$([N]([c])([C])[#6][#6]);!$([N]*~[#7,#8,#15,#16])]}</td></tr>
 * <tr><td>34</td><td>Ammonium</td><td>{@code [N+;!$([N]~[!#6]);!$(N=*);!$([N]*~[#7,#8,#15,#16])]}</td></tr>
 * <tr><td>35</td><td>Alkylthiol</td><td>{@code [SX2H][CX4;!$(C([SX2H])~[O,S,#7,#15])]}</td></tr>
 * <tr><td>36</td><td>Dialkylthioether</td><td>{@code [SX2]([CX4;!$(C([SX2])[O,S,#7,#15,F,Cl,Br,I])])[CX4;!$(C([SX2])[O,S,#7,#15])]}</td></tr>
 * <tr><td>37</td><td>Alkylarylthioether</td><td>{@code [SX2](c)[CX4;!$(C([SX2])[O,S,#7,#15])]}</td></tr>
 * <tr><td>38</td><td>Disulfide</td><td>{@code [SX2D2][SX2D2]}</td></tr>
 * <tr><td>39</td><td>1,2-Aminoalcohol</td><td>{@code [OX2H][CX4;!$(C([OX2H])[O,S,#7,#15,F,Cl,Br,I])][CX4;!$(C([N])[O,S,#7,#15])][NX3;!$(NC=[O,S,N])]}</td></tr>
 * <tr><td>40</td><td>1,2-Diol</td><td>{@code [OX2H][CX4;!$(C([OX2H])[O,S,#7,#15])][CX4;!$(C([OX2H])[O,S,#7,#15])][OX2H]}</td></tr>
 * <tr><td>41</td><td>1,1-Diol</td><td>{@code [OX2H][CX4;!$(C([OX2H])([OX2H])[O,S,#7,#15])][OX2H]}</td></tr>
 * <tr><td>42</td><td>Hydroperoxide</td><td>{@code [OX2H][OX2]}</td></tr>
 * <tr><td>43</td><td>Peroxo</td><td>{@code [OX2D2][OX2D2]}</td></tr>
 * <tr><td>44</td><td>Organolithium compounds</td><td>{@code [LiX1][#6,#14]}</td></tr>
 * <tr><td>45</td><td>Organomagnesium compounds</td><td>{@code [MgX2][#6,#14]}</td></tr>
 * <tr><td>46</td><td>Organometallic compounds</td><td>{@code [!#1;!#5;!#6;!#7;!#8;!#9;!#14;!#15;!#16;!#17;!#33;!#34;!#35;!#52;!#53;!#85]~[#6;!-]}</td></tr>
 * <tr><td>47</td><td>Aldehyde</td><td>{@code [$([CX3H][#6]),$([CX3H2])]=[OX1]}</td></tr>
 * <tr><td>48</td><td>Ketone</td><td>{@code [#6][CX3](=[OX1])[#6]}</td></tr>
 * <tr><td>49</td><td>Thioaldehyde</td><td>{@code [$([CX3H][#6]),$([CX3H2])]=[SX1]}</td></tr>
 * <tr><td>50</td><td>Thioketone</td><td>{@code [#6][CX3](=[SX1])[#6]}</td></tr>
 * <tr><td>51</td><td>Imine</td><td>{@code [NX2;$([N][#6]),$([NH]);!$([N][CX3]=[#7,#8,#15,#16])]=[CX3;$([CH2]),$([CH][#6]),$([C]([#6])[#6])]}</td></tr>
 * <tr><td>52</td><td>Immonium</td><td>{@code [NX3+;!$([N][!#6]);!$([N][CX3]=[#7,#8,#15,#16])]}</td></tr>
 * <tr><td>53</td><td>Oxime</td><td>{@code [NX2](=[CX3;$([CH2]),$([CH][#6]),$([C]([#6])[#6])])[OX2H]}</td></tr>
 * <tr><td>54</td><td>Oximether</td><td>{@code [NX2](=[CX3;$([CH2]),$([CH][#6]),$([C]([#6])[#6])])[OX2][#6;!$(C=[#7,#8])]}</td></tr>
 * <tr><td>55</td><td>Acetal</td><td>{@code [OX2]([#6;!$(C=[O,S,N])])[CX4;!$(C(O)(O)[!#6])][OX2][#6;!$(C=[O,S,N])]}</td></tr>
 * <tr><td>56</td><td>Hemiacetal</td><td>{@code [OX2H][CX4;!$(C(O)(O)[!#6])][OX2][#6;!$(C=[O,S,N])]}</td></tr>
 * <tr><td>57</td><td>Aminal</td><td>{@code [NX3v3;!$(NC=[#7,#8,#15,#16])]([#6])[CX4;!$(C(N)(N)[!#6])][NX3v3;!$(NC=[#7,#8,#15,#16])][#6]}</td></tr>
 * <tr><td>58</td><td>Hemiaminal</td><td>{@code [NX3v3;!$(NC=[#7,#8,#15,#16])]([#6])[CX4;!$(C(N)(N)[!#6])][OX2H]}</td></tr>
 * <tr><td>59</td><td>Thioacetal</td><td>{@code [SX2]([#6;!$(C=[O,S,N])])[CX4;!$(C(S)(S)[!#6])][SX2][#6;!$(C=[O,S,N])]}</td></tr>
 * <tr><td>60</td><td>Thiohemiacetal</td><td>{@code [SX2]([#6;!$(C=[O,S,N])])[CX4;!$(C(S)(S)[!#6])][OX2H]}</td></tr>
 * <tr><td>61</td><td>Halogen acetal like</td><td>{@code [NX3v3,SX2,OX2;!$(*C=[#7,#8,#15,#16])][CX4;!$(C([N,S,O])([N,S,O])[!#6])][FX1,ClX1,BrX1,IX1]}</td></tr>
 * <tr><td>62</td><td>Acetal like</td><td>{@code [NX3v3,SX2,OX2;!$(*C=[#7,#8,#15,#16])][CX4;!$(C([N,S,O])([N,S,O])[!#6])][FX1,ClX1,BrX1,IX1,NX3v3,SX2,OX2;!$(*C=[#7,#8,#15,#16])]}</td></tr>
 * <tr><td>63</td><td>Halogenmethylen ester and similar</td><td>{@code [NX3v3,SX2,OX2;$(**=[#7,#8,#15,#16])][CX4;!$(C([N,S,O])([N,S,O])[!#6])][FX1,ClX1,BrX1,IX1]}</td></tr>
 * <tr><td>64</td><td>NOS methylen ester and similar</td><td>{@code [NX3v3,SX2,OX2;$(**=[#7,#8,#15,#16])][CX4;!$(C([N,S,O])([N,S,O])[!#6])][NX3v3,SX2,OX2;!$(*C=[#7,#8,#15,#16])]}</td></tr>
 * <tr><td>65</td><td>Hetero methylen ester and similar</td><td>{@code [NX3v3,SX2,OX2;$(**=[#7,#8,#15,#16])][CX4;!$(C([N,S,O])([N,S,O])[!#6])][FX1,ClX1,BrX1,IX1,NX3v3,SX2,OX2;!$(*C=[#7,#8,#15,#16])]}</td></tr>
 * <tr><td>66</td><td>Cyanhydrine</td><td>{@code [NX1]#[CX2][CX4;$([CH2]),$([CH]([CX2])[#6]),$(C([CX2])([#6])[#6])][OX2H]}</td></tr>
 * <tr><td>67</td><td>Chloroalkene</td><td>{@code [ClX1][CX3]=[CX3]}</td></tr>
 * <tr><td>68</td><td>Fluoroalkene</td><td>{@code [FX1][CX3]=[CX3]}</td></tr>
 * <tr><td>69</td><td>Bromoalkene</td><td>{@code [BrX1][CX3]=[CX3]}</td></tr>
 * <tr><td>70</td><td>Iodoalkene</td><td>{@code [IX1][CX3]=[CX3]}</td></tr>
 * <tr><td>71</td><td>Enol</td><td>{@code [OX2H][CX3;$([H1]),$(C[#6])]=[CX3]}</td></tr>
 * <tr><td>72</td><td>Endiol</td><td>{@code [OX2H][CX3;$([H1]),$(C[#6])]=[CX3;$([H1]),$(C[#6])][OX2H]}</td></tr>
 * <tr><td>73</td><td>Enolether</td><td>{@code [OX2]([#6;!$(C=[N,O,S])])[CX3;$([H0][#6]),$([H1])]=[CX3]}</td></tr>
 * <tr><td>74</td><td>Enolester</td><td>{@code [OX2]([CX3]=[OX1])[#6X3;$([#6][#6]),$([H1])]=[#6X3;!$(C[OX2H])]}</td></tr>
 * <tr><td>75</td><td>Enamine</td><td>{@code [NX3;$([NH2][CX3]),$([NH1]([CX3])[#6]),$([N]([CX3])([#6])[#6]);!$([N]*=[#7,#8,#15,#16])][CX3;$([CH]),$([C][#6])]=[CX3]}</td></tr>
 * <tr><td>76</td><td>Thioenol</td><td>{@code [SX2H][CX3;$([H1]),$(C[#6])]=[CX3]}</td></tr>
 * <tr><td>77</td><td>Thioenolether</td><td>{@code [SX2]([#6;!$(C=[N,O,S])])[CX3;$(C[#6]),$([CH])]=[CX3]}</td></tr>
 * <tr><td>78</td><td>Acylchloride</td><td>{@code [CX3;$([R0][#6]),$([H1R0])](=[OX1])[ClX1]}</td></tr>
 * <tr><td>79</td><td>Acylfluoride</td><td>{@code [CX3;$([R0][#6]),$([H1R0])](=[OX1])[FX1]}</td></tr>
 * <tr><td>80</td><td>Acylbromide</td><td>{@code [CX3;$([R0][#6]),$([H1R0])](=[OX1])[BrX1]}</td></tr>
 * <tr><td>81</td><td>Acyliodide</td><td>{@code [CX3;$([R0][#6]),$([H1R0])](=[OX1])[IX1]}</td></tr>
 * <tr><td>82</td><td>Acylhalide</td><td>{@code [CX3;$([R0][#6]),$([H1R0])](=[OX1])[FX1,ClX1,BrX1,IX1]}</td></tr>
 * <tr><td>83</td><td>Carboxylic acid</td><td>{@code [CX3;$([R0][#6]),$([H1R0])](=[OX1])[$([OX2H]),$([OX1-])]}</td></tr>
 * <tr><td>84</td><td>Carboxylic ester</td><td>{@code [CX3;$([R0][#6]),$([H1R0])](=[OX1])[OX2][#6;!$(C=[O,N,S])]}</td></tr>
 * <tr><td>85</td><td>Lactone</td><td>{@code [#6][#6X3R](=[OX1])[#8X2][#6;!$(C=[O,N,S])]}</td></tr>
 * <tr><td>86</td><td>Carboxylic anhydride</td><td>{@code [CX3;$([H0][#6]),$([H1])](=[OX1])[#8X2][CX3;$([H0][#6]),$([H1])](=[OX1])}</td></tr>
 * <tr><td>87</td><td>Carboxylic acid derivative</td><td>{@code [$([#6X3H0][#6]),$([#6X3H])](=[!#6])[!#6]}</td></tr>
 * <tr><td>88</td><td>Carbothioic acid</td><td>{@code [CX3;!R;$([C][#6]),$([CH]);$([C](=[OX1])[$([SX2H]),$([SX1-])]),$([C](=[SX1])[$([OX2H]),$([OX1-])])]}</td></tr>
 * <tr><td>89</td><td>Carbothioic S ester</td><td>{@code [CX3;$([R0][#6]),$([H1R0])](=[OX1])[SX2][#6;!$(C=[O,N,S])]}</td></tr>
 * <tr><td>90</td><td>Carbothioic S lactone</td><td>{@code [#6][#6X3R](=[OX1])[#16X2][#6;!$(C=[O,N,S])]}</td></tr>
 * <tr><td>91</td><td>Carbothioic O ester</td><td>{@code [CX3;$([H0][#6]),$([H1])](=[SX1])[OX2][#6;!$(C=[O,N,S])]}</td></tr>
 * <tr><td>92</td><td>Carbothioic O lactone</td><td>{@code [#6][#6X3R](=[SX1])[#8X2][#6;!$(C=[O,N,S])]}</td></tr>
 * <tr><td>93</td><td>Carbothioic halide</td><td>{@code [CX3;$([H0][#6]),$([H1])](=[SX1])[FX1,ClX1,BrX1,IX1]}</td></tr>
 * <tr><td>94</td><td>Carbodithioic acid</td><td>{@code [CX3;!R;$([C][#6]),$([CH]);$([C](=[SX1])[SX2H])]}</td></tr>
 * <tr><td>95</td><td>Carbodithioic ester</td><td>{@code [CX3;!R;$([C][#6]),$([CH]);$([C](=[SX1])[SX2][#6;!$(C=[O,N,S])])]}</td></tr>
 * <tr><td>96</td><td>Carbodithiolactone</td><td>{@code [#6][#6X3R](=[SX1])[#16X2][#6;!$(C=[O,N,S])]}</td></tr>
 * <tr><td>97</td><td>Amide</td><td>{@code [CX3;$([R0][#6]),$([H1R0])](=[OX1])[#7X3;$([H2]),$([H1][#6;!$(C=[O,N,S])]),$([#7]([#6;!$(C=[O,N,S])])[#6;!$(C=[O,N,S])])]}</td></tr>
 * <tr><td>98</td><td>Primary amide</td><td>{@code [CX3;$([R0][#6]),$([H1R0])](=[OX1])[NX3H2]}</td></tr>
 * <tr><td>99</td><td>Secondary amide</td><td>{@code [CX3;$([R0][#6]),$([H1R0])](=[OX1])[#7X3H1][#6;!$(C=[O,N,S])]}</td></tr>
 * <tr><td>100</td><td>Tertiary amide</td><td>{@code [CX3;$([R0][#6]),$([H1R0])](=[OX1])[#7X3H0]([#6;!$(C=[O,N,S])])[#6;!$(C=[O,N,S])]}</td></tr>
 * <tr><td>101</td><td>Lactam</td><td>{@code [#6R][#6X3R](=[OX1])[#7X3;$([H1][#6;!$(C=[O,N,S])]),$([H0]([#6;!$(C=[O,N,S])])[#6;!$(C=[O,N,S])])]}</td></tr>
 * <tr><td>102</td><td>Alkyl imide</td><td>{@code [#6X3;$([H0][#6]),$([H1])](=[OX1])[#7X3H0]([#6])[#6X3;$([H0][#6]),$([H1])](=[OX1])}</td></tr>
 * <tr><td>103</td><td>N hetero imide</td><td>{@code [#6X3;$([H0][#6]),$([H1])](=[OX1])[#7X3H0]([!#6])[#6X3;$([H0][#6]),$([H1])](=[OX1])}</td></tr>
 * <tr><td>104</td><td>Imide acidic</td><td>{@code [#6X3;$([H0][#6]),$([H1])](=[OX1])[#7X3H1][#6X3;$([H0][#6]),$([H1])](=[OX1])}</td></tr>
 * <tr><td>105</td><td>Thioamide</td><td>{@code [$([CX3;!R][#6]),$([CX3H;!R])](=[SX1])[#7X3;$([H2]),$([H1][#6;!$(C=[O,N,S])]),$([#7]([#6;!$(C=[O,N,S])])[#6;!$(C=[O,N,S])])]}</td></tr>
 * <tr><td>106</td><td>Thiolactam</td><td>{@code [#6R][#6X3R](=[SX1])[#7X3;$([H1][#6;!$(C=[O,N,S])]),$([H0]([#6;!$(C=[O,N,S])])[#6;!$(C=[O,N,S])])]}</td></tr>
 * <tr><td>107</td><td>Oximester</td><td>{@code [#6X3;$([H0][#6]),$([H1])](=[OX1])[#8X2][#7X2]=,:[#6X3;$([H0]([#6])[#6]),$([H1][#6]),$([H2])]}</td></tr>
 * <tr><td>108</td><td>Amidine</td><td>{@code [NX3;!$(NC=[O,S])][CX3;$([CH]),$([C][#6])]=[NX2;!$(NC=[O,S])]}</td></tr>
 * <tr><td>109</td><td>Hydroxamic acid</td><td>{@code [CX3;$([H0][#6]),$([H1])](=[OX1])[#7X3;$([H1]),$([H0][#6;!$(C=[O,N,S])])][$([OX2H]),$([OX1-])]}</td></tr>
 * <tr><td>110</td><td>Hydroxamic acid ester</td><td>{@code [CX3;$([H0][#6]),$([H1])](=[OX1])[#7X3;$([H1]),$([H0][#6;!$(C=[O,N,S])])][OX2][#6;!$(C=[O,N,S])]}</td></tr>
 * <tr><td>111</td><td>Imidoacid</td><td>{@code [CX3R0;$([H0][#6]),$([H1])](=[NX2;$([H1]),$([H0][#6;!$(C=[O,N,S])])])[$([OX2H]),$([OX1-])]}</td></tr>
 * <tr><td>112</td><td>Imidoacid cyclic</td><td>{@code [#6R][#6X3R](=,:[#7X2;$([H1]),$([H0][#6;!$(C=[O,N,S])])])[$([OX2H]),$([OX1-])]}</td></tr>
 * <tr><td>113</td><td>Imidoester</td><td>{@code [CX3R0;$([H0][#6]),$([H1])](=[NX2;$([H1]),$([H0][#6;!$(C=[O,N,S])])])[OX2][#6;!$(C=[O,N,S])]}</td></tr>
 * <tr><td>114</td><td>Imidolactone</td><td>{@code [#6R][#6X3R](=,:[#7X2;$([H1]),$([H0][#6;!$(C=[O,N,S])])])[OX2][#6;!$(C=[O,N,S])]}</td></tr>
 * <tr><td>115</td><td>Imidothioacid</td><td>{@code [CX3R0;$([H0][#6]),$([H1])](=[NX2;$([H1]),$([H0][#6;!$(C=[O,N,S])])])[$([SX2H]),$([SX1-])]}</td></tr>
 * <tr><td>116</td><td>Imidothioacid cyclic</td><td>{@code [#6R][#6X3R](=,:[#7X2;$([H1]),$([H0][#6;!$(C=[O,N,S])])])[$([SX2H]),$([SX1-])]}</td></tr>
 * <tr><td>117</td><td>Imidothioester</td><td>{@code [CX3R0;$([H0][#6]),$([H1])](=[NX2;$([H1]),$([H0][#6;!$(C=[O,N,S])])])[SX2][#6;!$(C=[O,N,S])]}</td></tr>
 * <tr><td>118</td><td>Imidothiolactone</td><td>{@code [#6R][#6X3R](=,:[#7X2;$([H1]),$([H0][#6;!$(C=[O,N,S])])])[SX2][#6;!$(C=[O,N,S])]}</td></tr>
 * <tr><td>119</td><td>Amidine</td><td>{@code [#7X3v3;!$(N([#6X3]=[#7X2])C=[O,S])][CX3R0;$([H1]),$([H0][#6])]=[NX2v3;!$(N(=[#6X3][#7X3])C=[O,S])]}</td></tr>
 * <tr><td>120</td><td>Imidolactam</td><td>{@code [#6][#6X3R;$([H0](=[NX2;!$(N(=[#6X3][#7X3])C=[O,S])])[#7X3;!$(N([#6X3]=[#7X2])C=[O,S])]),$([H0](-[NX3;!$(N([#6X3]=[#7X2])C=[O,S])])=,:[#7X2;!$(N(=[#6X3][#7X3])C=[O,S])])]}</td></tr>
 * <tr><td>121</td><td>Imidoylhalide</td><td>{@code [CX3R0;$([H0][#6]),$([H1])](=[NX2;$([H1]),$([H0][#6;!$(C=[O,N,S])])])[FX1,ClX1,BrX1,IX1]}</td></tr>
 * <tr><td>122</td><td>Imidoylhalide cyclic</td><td>{@code [#6R][#6X3R](=,:[#7X2;$([H1]),$([H0][#6;!$(C=[O,N,S])])])[FX1,ClX1,BrX1,IX1]}</td></tr>
 * <tr><td>123</td><td>Amidrazone</td><td>{@code [$([$([#6X3][#6]),$([#6X3H])](=[#7X2v3])[#7X3v3][#7X3v3]),$([$([#6X3][#6]),$([#6X3H])]([#7X3v3])=[#7X2v3][#7X3v3])]}</td></tr>
 * <tr><td>124</td><td>Alpha aminoacid</td><td>{@code [NX3,NX4+;!$([N]~[!#6]);!$([N]*~[#7,#8,#15,#16])][C][CX3](=[OX1])[OX2H,OX1-]}</td></tr>
 * <tr><td>125</td><td>Alpha hydroxyacid</td><td>{@code [OX2H][C][CX3](=[OX1])[OX2H,OX1-]}</td></tr>
 * <tr><td>126</td><td>Peptide middle</td><td>{@code [NX3;$([N][CX3](=[OX1])[C][NX3,NX4+])][C][CX3](=[OX1])[NX3;$([N][C][CX3](=[OX1])[NX3,OX2,OX1-])]}</td></tr>
 * <tr><td>127</td><td>Peptide C term</td><td>{@code [NX3;$([N][CX3](=[OX1])[C][NX3,NX4+])][C][CX3](=[OX1])[OX2H,OX1-]}</td></tr>
 * <tr><td>128</td><td>Peptide N term</td><td>{@code [NX3,NX4+;!$([N]~[!#6]);!$([N]*~[#7,#8,#15,#16])][C][CX3](=[OX1])[NX3;$([N][C][CX3](=[OX1])[NX3,OX2,OX1-])]}</td></tr>
 * <tr><td>129</td><td>Carboxylic orthoester</td><td>{@code [#6][OX2][CX4;$(C[#6]),$([CH])]([OX2][#6])[OX2][#6]}</td></tr>
 * <tr><td>130</td><td>Ketene</td><td>{@code [CX3]=[CX2]=[OX1]}</td></tr>
 * <tr><td>131</td><td>Ketenacetal</td><td>{@code [#7X2,#8X3,#16X2;$(*[#6,#14])][#6X3]([#7X2,#8X3,#16X2;$(*[#6,#14])])=[#6X3]}</td></tr>
 * <tr><td>132</td><td>Nitrile</td><td>{@code [NX1]#[CX2]}</td></tr>
 * <tr><td>133</td><td>Isonitrile</td><td>{@code [CX1-]#[NX2+]}</td></tr>
 * <tr><td>134</td><td>Vinylogous carbonyl or carboxyl derivative</td><td>{@code [#6X3](=[OX1])[#6X3]=,:[#6X3][#7,#8,#16,F,Cl,Br,I]}</td></tr>
 * <tr><td>135</td><td>Vinylogous acid</td><td>{@code [#6X3](=[OX1])[#6X3]=,:[#6X3][$([OX2H]),$([OX1-])]}</td></tr>
 * <tr><td>136</td><td>Vinylogous ester</td><td>{@code [#6X3](=[OX1])[#6X3]=,:[#6X3][#6;!$(C=[O,N,S])]}</td></tr>
 * <tr><td>137</td><td>Vinylogous amide</td><td>{@code [#6X3](=[OX1])[#6X3]=,:[#6X3][#7X3;$([H2]),$([H1][#6;!$(C=[O,N,S])]),$([#7]([#6;!$(C=[O,N,S])])[#6;!$(C=[O,N,S])])]}</td></tr>
 * <tr><td>138</td><td>Vinylogous halide</td><td>{@code [#6X3](=[OX1])[#6X3]=,:[#6X3][FX1,ClX1,BrX1,IX1]}</td></tr>
 * <tr><td>139</td><td>Carbonic acid dieester</td><td>{@code [#6;!$(C=[O,N,S])][#8X2][#6X3](=[OX1])[#8X2][#6;!$(C=[O,N,S])]}</td></tr>
 * <tr><td>140</td><td>Carbonic acid esterhalide</td><td>{@code [#6;!$(C=[O,N,S])][OX2;!R][CX3](=[OX1])[OX2][FX1,ClX1,BrX1,IX1]}</td></tr>
 * <tr><td>141</td><td>Carbonic acid monoester</td><td>{@code [#6;!$(C=[O,N,S])][OX2;!R][CX3](=[OX1])[$([OX2H]),$([OX1-])]}</td></tr>
 * <tr><td>142</td><td>Carbonic acid derivatives</td><td>{@code [!#6][#6X3](=[!#6])[!#6]}</td></tr>
 * <tr><td>143</td><td>Thiocarbonic acid dieester</td><td>{@code [#6;!$(C=[O,N,S])][#8X2][#6X3](=[SX1])[#8X2][#6;!$(C=[O,N,S])]}</td></tr>
 * <tr><td>144</td><td>Thiocarbonic acid esterhalide</td><td>{@code [#6;!$(C=[O,N,S])][OX2;!R][CX3](=[SX1])[OX2][FX1,ClX1,BrX1,IX1]}</td></tr>
 * <tr><td>145</td><td>Thiocarbonic acid monoester</td><td>{@code [#6;!$(C=[O,N,S])][OX2;!R][CX3](=[SX1])[$([OX2H]),$([OX1-])]}</td></tr>
 * <tr><td>146</td><td>Urea</td><td>{@code [#7X3;!$([#7][!#6])][#6X3](=[OX1])[#7X3;!$([#7][!#6])]}</td></tr>
 * <tr><td>147</td><td>Thiourea</td><td>{@code [#7X3;!$([#7][!#6])][#6X3](=[SX1])[#7X3;!$([#7][!#6])]}</td></tr>
 * <tr><td>148</td><td>Isourea</td><td>{@code [#7X2;!$([#7][!#6])]=,:[#6X3]([#8X2&!$([#8][!#6]),OX1-])[#7X3;!$([#7][!#6])]}</td></tr>
 * <tr><td>149</td><td>Isothiourea</td><td>{@code [#7X2;!$([#7][!#6])]=,:[#6X3]([#16X2&!$([#16][!#6]),SX1-])[#7X3;!$([#7][!#6])]}</td></tr>
 * <tr><td>150</td><td>Guanidine</td><td>{@code [N;v3X3,v4X4+][CX3](=[N;v3X2,v4X3+])[N;v3X3,v4X4+]}</td></tr>
 * <tr><td>151</td><td>Carbaminic acid</td><td>{@code [NX3]C(=[OX1])[O;X2H,X1-]}</td></tr>
 * <tr><td>152</td><td>Urethan</td><td>{@code [#7X3][#6](=[OX1])[#8X2][#6]}</td></tr>
 * <tr><td>153</td><td>Biuret</td><td>{@code [#7X3][#6](=[OX1])[#7X3][#6](=[OX1])[#7X3]}</td></tr>
 * <tr><td>154</td><td>Semicarbazide</td><td>{@code [#7X3][#7X3][#6X3]([#7X3;!$([#7][#7])])=[OX1]}</td></tr>
 * <tr><td>155</td><td>Carbazide</td><td>{@code [#7X3][#7X3][#6X3]([#7X3][#7X3])=[OX1]}</td></tr>
 * <tr><td>156</td><td>Semicarbazone</td><td>{@code [#7X2](=[#6])[#7X3][#6X3]([#7X3;!$([#7][#7])])=[OX1]}</td></tr>
 * <tr><td>157</td><td>Carbazone</td><td>{@code [#7X2](=[#6])[#7X3][#6X3]([#7X3][#7X3])=[OX1]}</td></tr>
 * <tr><td>158</td><td>Thiosemicarbazide</td><td>{@code [#7X3][#7X3][#6X3]([#7X3;!$([#7][#7])])=[SX1]}</td></tr>
 * <tr><td>159</td><td>Thiocarbazide</td><td>{@code [#7X3][#7X3][#6X3]([#7X3][#7X3])=[SX1]}</td></tr>
 * <tr><td>160</td><td>Thiosemicarbazone</td><td>{@code [#7X2](=[#6])[#7X3][#6X3]([#7X3;!$([#7][#7])])=[SX1]}</td></tr>
 * <tr><td>161</td><td>Thiocarbazone</td><td>{@code [#7X2](=[#6])[#7X3][#6X3]([#7X3][#7X3])=[SX1]}</td></tr>
 * <tr><td>162</td><td>Isocyanate</td><td>{@code [NX2]=[CX2]=[OX1]}</td></tr>
 * <tr><td>163</td><td>Cyanate</td><td>{@code [OX2][CX2]#[NX1]}</td></tr>
 * <tr><td>164</td><td>Isothiocyanate</td><td>{@code [NX2]=[CX2]=[SX1]}</td></tr>
 * <tr><td>165</td><td>Thiocyanate</td><td>{@code [SX2][CX2]#[NX1]}</td></tr>
 * <tr><td>166</td><td>Carbodiimide</td><td>{@code [NX2]=[CX2]=[NX2]}</td></tr>
 * <tr><td>167</td><td>Orthocarbonic derivatives</td><td>{@code [CX4H0]([O,S,#7])([O,S,#7])([O,S,#7])[O,S,#7,F,Cl,Br,I]}</td></tr>
 * <tr><td>168</td><td>Phenol</td><td>{@code [OX2H][c]}</td></tr>
 * <tr><td>169</td><td>1,2-Diphenol</td><td>{@code [OX2H][c][c][OX2H]}</td></tr>
 * <tr><td>170</td><td>Arylchloride</td><td>{@code [Cl][c]}</td></tr>
 * <tr><td>171</td><td>Arylfluoride</td><td>{@code [F][c]}</td></tr>
 * <tr><td>172</td><td>Arylbromide</td><td>{@code [Br][c]}</td></tr>
 * <tr><td>173</td><td>Aryliodide</td><td>{@code [I][c]}</td></tr>
 * <tr><td>174</td><td>Arylthiol</td><td>{@code [SX2H][c]}</td></tr>
 * <tr><td>175</td><td>Iminoarene</td><td>{@code [c]=[NX2;$([H1]),$([H0][#6;!$([C]=[N,S,O])])]}</td></tr>
 * <tr><td>176</td><td>Oxoarene</td><td>{@code [c]=[OX1]}</td></tr>
 * <tr><td>177</td><td>Thioarene</td><td>{@code [c]=[SX1]}</td></tr>
 * <tr><td>178</td><td>Hetero N basic H</td><td>{@code [nX3H1+0]}</td></tr>
 * <tr><td>179</td><td>Hetero N basic no H</td><td>{@code [nX3H0+0]}</td></tr>
 * <tr><td>180</td><td>Hetero N nonbasic</td><td>{@code [nX2,nX3+]}</td></tr>
 * <tr><td>181</td><td>Hetero O</td><td>{@code [o]}</td></tr>
 * <tr><td>182</td><td>Hetero S</td><td>{@code [sX2]}</td></tr>
 * <tr><td>183</td><td>Heteroaromatic</td><td>{@code [a;!c]}</td></tr>
 * <tr><td>184</td><td>Nitrite</td><td>{@code [NX2](=[OX1])[O;$([X2]),$([X1-])]}</td></tr>
 * <tr><td>185</td><td>Thionitrite</td><td>{@code [SX2][NX2]=[OX1]}</td></tr>
 * <tr><td>186</td><td>Nitrate</td><td>{@code [$([NX3](=[OX1])(=[OX1])[O;$([X2]),$([X1-])]),$([NX3+]([OX1-])(=[OX1])[O;$([X2]),$([X1-])])]}</td></tr>
 * <tr><td>187</td><td>Nitro</td><td>{@code [$([NX3](=O)=O),$([NX3+](=O)[O-])][!#8]}</td></tr>
 * <tr><td>188</td><td>Nitroso</td><td>{@code [NX2](=[OX1])[!#7;!#8]}</td></tr>
 * <tr><td>189</td><td>Azide</td><td>{@code [NX1]~[NX2]~[NX2,NX1]}</td></tr>
 * <tr><td>190</td><td>Acylazide</td><td>{@code [CX3](=[OX1])[NX2]~[NX2]~[NX1]}</td></tr>
 * <tr><td>191</td><td>Diazo</td><td>{@code [$([#6]=[NX2+]=[NX1-]),$([#6-]-[NX2+]#[NX1])]}</td></tr>
 * <tr><td>192</td><td>Diazonium</td><td>{@code [#6][NX2+]#[NX1]}</td></tr>
 * <tr><td>193</td><td>Nitrosamine</td><td>{@code [#7;!$(N*=O)][NX2]=[OX1]}</td></tr>
 * <tr><td>194</td><td>Nitrosamide</td><td>{@code [NX2](=[OX1])N-*=O}</td></tr>
 * <tr><td>195</td><td>N-Oxide</td><td>{@code [$([#7+][OX1-]),$([#7v5]=[OX1]);!$([#7](~[O])~[O]);!$([#7]=[#7])]}</td></tr>
 * <tr><td>196</td><td>Hydrazine</td><td>{@code [NX3;$([H2]),$([H1][#6]),$([H0]([#6])[#6]);!$(NC=[O,N,S])][NX3;$([H2]),$([H1][#6]),$([H0]([#6])[#6]);!$(NC=[O,N,S])]}</td></tr>
 * <tr><td>197</td><td>Hydrazone</td><td>{@code [NX3;$([H2]),$([H1][#6]),$([H0]([#6])[#6]);!$(NC=[O,N,S])][NX2]=[#6]}</td></tr>
 * <tr><td>198</td><td>Hydroxylamine</td><td>{@code [NX3;$([H2]),$([H1][#6]),$([H0]([#6])[#6]);!$(NC=[O,N,S])][OX2;$([H1]),$(O[#6;!$(C=[N,O,S])])]}</td></tr>
 * <tr><td>199</td><td>Sulfon</td><td>{@code [$([SX4](=[OX1])(=[OX1])([#6])[#6]),$([SX4+2]([OX1-])([OX1-])([#6])[#6])]}</td></tr>
 * <tr><td>200</td><td>Sulfoxide</td><td>{@code [$([SX3](=[OX1])([#6])[#6]),$([SX3+]([OX1-])([#6])[#6])]}</td></tr>
 * <tr><td>201</td><td>Sulfonium</td><td>{@code [S+;!$([S]~[!#6]);!$([S]*~[#7,#8,#15,#16])]}</td></tr>
 * <tr><td>202</td><td>Sulfuric acid</td><td>{@code [SX4](=[OX1])(=[OX1])([$([OX2H]),$([OX1-])])[$([OX2H]),$([OX1-])]}</td></tr>
 * <tr><td>203</td><td>Sulfuric monoester</td><td>{@code [SX4](=[OX1])(=[OX1])([$([OX2H]),$([OX1-])])[OX2][#6;!$(C=[O,N,S])]}</td></tr>
 * <tr><td>204</td><td>Sulfuric diester</td><td>{@code [SX4](=[OX1])(=[OX1])([OX2][#6;!$(C=[O,N,S])])[OX2][#6;!$(C=[O,N,S])]}</td></tr>
 * <tr><td>205</td><td>Sulfuric monoamide</td><td>{@code [SX4](=[OX1])(=[OX1])([#7X3;$([H2]),$([H1][#6;!$(C=[O,N,S])]),$([#7]([#6;!$(C=[O,N,S])])[#6;!$(C=[O,N,S])])])[$([OX2H]),$([OX1-])]}</td></tr>
 * <tr><td>206</td><td>Sulfuric diamide</td><td>{@code [SX4](=[OX1])(=[OX1])([#7X3;$([H2]),$([H1][#6;!$(C=[O,N,S])]),$([#7]([#6;!$(C=[O,N,S])])[#6;!$(C=[O,N,S])])])[#7X3;$([H2]),$([H1][#6;!$(C=[O,N,S])]),$([#7]([#6;!$(C=[O,N,S])])[#6;!$(C=[O,N,S])])]}</td></tr>
 * <tr><td>207</td><td>Sulfuric esteramide</td><td>{@code [SX4](=[OX1])(=[OX1])([#7X3][#6;!$(C=[O,N,S])])[OX2][#6;!$(C=[O,N,S])]}</td></tr>
 * <tr><td>208</td><td>Sulfuric derivative</td><td>{@code [SX4D4](=[!#6])(=[!#6])([!#6])[!#6]}</td></tr>
 * <tr><td>209</td><td>Sulfonic acid</td><td>{@code [SX4;$([H1]),$([H0][#6])](=[OX1])(=[OX1])[$([OX2H]),$([OX1-])]}</td></tr>
 * <tr><td>210</td><td>Sulfonamide</td><td>{@code [SX4;$([H1]),$([H0][#6])](=[OX1])(=[OX1])[#7X3;$([H2]),$([H1][#6;!$(C=[O,N,S])]),$([#7]([#6;!$(C=[O,N,S])])[#6;!$(C=[O,N,S])])]}</td></tr>
 * <tr><td>211</td><td>Sulfonic ester</td><td>{@code [SX4;$([H1]),$([H0][#6])](=[OX1])(=[OX1])[OX2][#6;!$(C=[O,N,S])]}</td></tr>
 * <tr><td>212</td><td>Sulfonic halide</td><td>{@code [SX4;$([H1]),$([H0][#6])](=[OX1])(=[OX1])[FX1,ClX1,BrX1,IX1]}</td></tr>
 * <tr><td>213</td><td>Sulfonic derivative</td><td>{@code [SX4;$([H1]),$([H0][#6])](=[!#6])(=[!#6])[!#6]}</td></tr>
 * <tr><td>214</td><td>Sulfinic acid</td><td>{@code [SX3;$([H1]),$([H0][#6])](=[OX1])[$([OX2H]),$([OX1-])]}</td></tr>
 * <tr><td>215</td><td>Sulfinic amide</td><td>{@code [SX3;$([H1]),$([H0][#6])](=[OX1])[#7X3;$([H2]),$([H1][#6;!$(C=[O,N,S])]),$([#7]([#6;!$(C=[O,N,S])])[#6;!$(C=[O,N,S])])]}</td></tr>
 * <tr><td>216</td><td>Sulfinic ester</td><td>{@code [SX3;$([H1]),$([H0][#6])](=[OX1])[OX2][#6;!$(C=[O,N,S])]}</td></tr>
 * <tr><td>217</td><td>Sulfinic halide</td><td>{@code [SX3;$([H1]),$([H0][#6])](=[OX1])[FX1,ClX1,BrX1,IX1]}</td></tr>
 * <tr><td>218</td><td>Sulfinic derivative</td><td>{@code [SX3;$([H1]),$([H0][#6])](=[!#6])[!#6]}</td></tr>
 * <tr><td>219</td><td>Sulfenic acid</td><td>{@code [SX2;$([H1]),$([H0][#6])][$([OX2H]),$([OX1-])]}</td></tr>
 * <tr><td>220</td><td>Sulfenic amide</td><td>{@code [SX2;$([H1]),$([H0][#6])][#7X3;$([H2]),$([H1][#6;!$(C=[O,N,S])]),$([#7]([#6;!$(C=[O,N,S])])[#6;!$(C=[O,N,S])])]}</td></tr>
 * <tr><td>221</td><td>Sulfenic ester</td><td>{@code [SX2;$([H1]),$([H0][#6])][OX2][#6;!$(C=[O,N,S])]}</td></tr>
 * <tr><td>222</td><td>Sulfenic halide</td><td>{@code [SX2;$([H1]),$([H0][#6])][FX1,ClX1,BrX1,IX1]}</td></tr>
 * <tr><td>223</td><td>Sulfenic derivative</td><td>{@code [SX2;$([H1]),$([H0][#6])][!#6]}</td></tr>
 * <tr><td>224</td><td>Phosphine</td><td>{@code [PX3;$([H3]),$([H2][#6]),$([H1]([#6])[#6]),$([H0]([#6])([#6])[#6])]}</td></tr>
 * <tr><td>225</td><td>Phosphine oxide</td><td>{@code [PX4;$([H3]=[OX1]),$([H2](=[OX1])[#6]),$([H1](=[OX1])([#6])[#6]),$([H0](=[OX1])([#6])([#6])[#6])]}</td></tr>
 * <tr><td>226</td><td>Phosphonium</td><td>{@code [P+;!$([P]~[!#6]);!$([P]*~[#7,#8,#15,#16])]}</td></tr>
 * <tr><td>227</td><td>Phosphorylen</td><td>{@code [PX4;$([H3]=[CX3]),$([H2](=[CX3])[#6]),$([H1](=[CX3])([#6])[#6]),$([H0](=[CX3])([#6])([#6])[#6])]}</td></tr>
 * <tr><td>228</td><td>Phosphonic acid</td><td>{@code [PX4;$([H1]),$([H0][#6])](=[OX1])([$([OX2H]),$([OX1-])])[$([OX2H]),$([OX1-])]}</td></tr>
 * <tr><td>229</td><td>Phosphonic monoester</td><td>{@code [PX4;$([H1]),$([H0][#6])](=[OX1])([$([OX2H]),$([OX1-])])[OX2][#6;!$(C=[O,N,S])]}</td></tr>
 * <tr><td>230</td><td>Phosphonic diester</td><td>{@code [PX4;$([H1]),$([H0][#6])](=[OX1])([OX2][#6;!$(C=[O,N,S])])[OX2][#6;!$(C=[O,N,S])]}</td></tr>
 * <tr><td>231</td><td>Phosphonic monoamide</td><td>{@code [PX4;$([H1]),$([H0][#6])](=[OX1])([$([OX2H]),$([OX1-])])[#7X3;$([H2]),$([H1][#6;!$(C=[O,N,S])]),$([#7]([#6;!$(C=[O,N,S])])[#6;!$(C=[O,N,S])])]}</td></tr>
 * <tr><td>232</td><td>Phosphonic diamide</td><td>{@code [PX4;$([H1]),$([H0][#6])](=[OX1])([#7X3;$([H2]),$([H1][#6;!$(C=[O,N,S])]),$([#7]([#6;!$(C=[O,N,S])])[#6;!$(C=[O,N,S])])])[#7X3;$([H2]),$([H1][#6;!$(C=[O,N,S])]),$([#7]([#6;!$(C=[O,N,S])])[#6;!$(C=[O,N,S])])]}</td></tr>
 * <tr><td>233</td><td>Phosphonic esteramide</td><td>{@code [PX4;$([H1]),$([H0][#6])](=[OX1])([OX2][#6;!$(C=[O,N,S])])[#7X3;$([H2]),$([H1][#6;!$(C=[O,N,S])]),$([#7]([#6;!$(C=[O,N,S])])[#6;!$(C=[O,N,S])])]}</td></tr>
 * <tr><td>234</td><td>Phosphonic acid derivative</td><td>{@code [PX4;$([H1]),$([H0][#6])](=[!#6])([!#6])[!#6]}</td></tr>
 * <tr><td>235</td><td>Phosphoric acid</td><td>{@code [PX4D4](=[OX1])([$([OX2H]),$([OX1-])])([$([OX2H]),$([OX1-])])[$([OX2H]),$([OX1-])]}</td></tr>
 * <tr><td>236</td><td>Phosphoric monoester</td><td>{@code [PX4D4](=[OX1])([$([OX2H]),$([OX1-])])([$([OX2H]),$([OX1-])])[OX2][#6;!$(C=[O,N,S])]}</td></tr>
 * <tr><td>237</td><td>Phosphoric diester</td><td>{@code [PX4D4](=[OX1])([$([OX2H]),$([OX1-])])([OX2][#6;!$(C=[O,N,S])])[OX2][#6;!$(C=[O,N,S])]}</td></tr>
 * <tr><td>238</td><td>Phosphoric triester</td><td>{@code [PX4D4](=[OX1])([OX2][#6;!$(C=[O,N,S])])([OX2][#6;!$(C=[O,N,S])])[OX2][#6;!$(C=[O,N,S])]}</td></tr>
 * <tr><td>239</td><td>Phosphoric monoamide</td><td>{@code [PX4D4](=[OX1])([$([OX2H]),$([OX1-])])([$([OX2H]),$([OX1-])])[#7X3;$([H2]),$([H1][#6;!$(C=[O,N,S])]),$([#7]([#6;!$(C=[O,N,S])])[#6;!$(C=[O,N,S])])]}</td></tr>
 * <tr><td>240</td><td>Phosphoric diamide</td><td>{@code [PX4D4](=[OX1])([$([OX2H]),$([OX1-])])([#7X3;$([H2]),$([H1][#6;!$(C=[O,N,S])]),$([#7]([#6;!$(C=[O,N,S])])[#6;!$(C=[O,N,S])])])[#7X3;$([H2]),$([H1][#6;!$(C=[O,N,S])]),$([#7]([#6;!$(C=[O,N,S])])[#6;!$(C=[O,N,S])])]}</td></tr>
 * <tr><td>241</td><td>Phosphoric triamide</td><td>{@code [PX4D4](=[OX1])([#7X3;$([H2]),$([H1][#6;!$(C=[O,N,S])]),$([#7]([#6;!$(C=[O,N,S])])[#6;!$(C=[O,N,S])])])([#7X3;$([H2]),$([H1][#6;!$(C=[O,N,S])]),$([#7]([#6;!$(C=[O,N,S])])[#6;!$(C=[O,N,S])])])[#7X3;$([H2]),$([H1][#6;!$(C=[O,N,S])]),$([#7]([#6;!$(C=[O,N,S])])[#6;!$(C=[O,N,S])])]}</td></tr>
 * <tr><td>242</td><td>Phosphoric monoestermonoamide</td><td>{@code [PX4D4](=[OX1])([$([OX2H]),$([OX1-])])([OX2][#6;!$(C=[O,N,S])])[#7X3;$([H2]),$([H1][#6;!$(C=[O,N,S])]),$([#7]([#6;!$(C=[O,N,S])])[#6;!$(C=[O,N,S])])]}</td></tr>
 * <tr><td>243</td><td>Phosphoric diestermonoamide</td><td>{@code [PX4D4](=[OX1])([OX2][#6;!$(C=[O,N,S])])([OX2][#6;!$(C=[O,N,S])])[#7X3;$([H2]),$([H1][#6;!$(C=[O,N,S])]),$([#7]([#6;!$(C=[O,N,S])])[#6;!$(C=[O,N,S])])]}</td></tr>
 * <tr><td>244</td><td>Phosphoric monoesterdiamide</td><td>{@code [PX4D4](=[OX1])([OX2][#6;!$(C=[O,N,S])])([#7X3;$([H2]),$([H1][#6;!$(C=[O,N,S])]),$([#7]([#6;!$(C=[O,N,S])])[#6;!$(C=[O,N,S])])])[#7X3;$([H2]),$([H1][#6;!$(C=[O,N,S])]),$([#7]([#6;!$(C=[O,N,S])])[#6;!$(C=[O,N,S])])]}</td></tr>
 * <tr><td>245</td><td>Phosphoric acid derivative</td><td>{@code [PX4D4](=[!#6])([!#6])([!#6])[!#6]}</td></tr>
 * <tr><td>246</td><td>Phosphinic acid</td><td>{@code [PX4;$([H2]),$([H1][#6]),$([H0]([#6])[#6])](=[OX1])[$([OX2H]),$([OX1-])]}</td></tr>
 * <tr><td>247</td><td>Phosphinic ester</td><td>{@code [PX4;$([H2]),$([H1][#6]),$([H0]([#6])[#6])](=[OX1])[OX2][#6;!$(C=[O,N,S])]}</td></tr>
 * <tr><td>248</td><td>Phosphinic amide</td><td>{@code [PX4;$([H2]),$([H1][#6]),$([H0]([#6])[#6])](=[OX1])[#7X3;$([H2]),$([H1][#6;!$(C=[O,N,S])]),$([#7]([#6;!$(C=[O,N,S])])[#6;!$(C=[O,N,S])])]}</td></tr>
 * <tr><td>249</td><td>Phosphinic acid derivative</td><td>{@code [PX4;$([H2]),$([H1][#6]),$([H0]([#6])[#6])](=[!#6])[!#6]}</td></tr>
 * <tr><td>250</td><td>Phosphonous acid</td><td>{@code [PX3;$([H1]),$([H0][#6])]([$([OX2H]),$([OX1-])])[$([OX2H]),$([OX1-])]}</td></tr>
 * <tr><td>251</td><td>Phosphonous monoester</td><td>{@code [PX3;$([H1]),$([H0][#6])]([$([OX2H]),$([OX1-])])[OX2][#6;!$(C=[O,N,S])]}</td></tr>
 * <tr><td>252</td><td>Phosphonous diester</td><td>{@code [PX3;$([H1]),$([H0][#6])]([OX2][#6;!$(C=[O,N,S])])[OX2][#6;!$(C=[O,N,S])]}</td></tr>
 * <tr><td>253</td><td>Phosphonous monoamide</td><td>{@code [PX3;$([H1]),$([H0][#6])]([$([OX2H]),$([OX1-])])[#7X3;$([H2]),$([H1][#6;!$(C=[O,N,S])]),$([#7]([#6;!$(C=[O,N,S])])[#6;!$(C=[O,N,S])])]}</td></tr>
 * <tr><td>254</td><td>Phosphonous diamide</td><td>{@code [PX3;$([H1]),$([H0][#6])]([#7X3;$([H2]),$([H1][#6;!$(C=[O,N,S])]),$([#7]([#6;!$(C=[O,N,S])])[#6;!$(C=[O,N,S])])])[#7X3;$([H2]),$([H1][#6;!$(C=[O,N,S])]),$([#7]([#6;!$(C=[O,N,S])])[#6;!$(C=[O,N,S])])]}</td></tr>
 * <tr><td>255</td><td>Phosphonous esteramide</td><td>{@code [PX3;$([H1]),$([H0][#6])]([OX2][#6;!$(C=[O,N,S])])[#7X3;$([H2]),$([H1][#6;!$(C=[O,N,S])]),$([#7]([#6;!$(C=[O,N,S])])[#6;!$(C=[O,N,S])])]}</td></tr>
 * <tr><td>256</td><td>Phosphonous derivatives</td><td>{@code [PX3;$([D2]),$([D3][#6])]([!#6])[!#6]}</td></tr>
 * <tr><td>257</td><td>Phosphinous acid</td><td>{@code [PX3;$([H2]),$([H1][#6]),$([H0]([#6])[#6])][$([OX2H]),$([OX1-])]}</td></tr>
 * <tr><td>258</td><td>Phosphinous ester</td><td>{@code [PX3;$([H2]),$([H1][#6]),$([H0]([#6])[#6])][OX2][#6;!$(C=[O,N,S])]}</td></tr>
 * <tr><td>259</td><td>Phosphinous amide</td><td>{@code [PX3;$([H2]),$([H1][#6]),$([H0]([#6])[#6])][#7X3;$([H2]),$([H1][#6;!$(C=[O,N,S])]),$([#7]([#6;!$(C=[O,N,S])])[#6;!$(C=[O,N,S])])]}</td></tr>
 * <tr><td>260</td><td>Phosphinous derivatives</td><td>{@code [PX3;$([H2]),$([H1][#6]),$([H0]([#6])[#6])][!#6]}</td></tr>
 * <tr><td>261</td><td>Quart silane</td><td>{@code [SiX4]([#6])([#6])([#6])[#6]}</td></tr>
 * <tr><td>262</td><td>Non-quart silane</td><td>{@code [SiX4;$([H1]([#6])([#6])[#6]),$([H2]([#6])[#6]),$([H3][#6]),$([H4])]}</td></tr>
 * <tr><td>263</td><td>Silylmonohalide</td><td>{@code [SiX4]([FX1,ClX1,BrX1,IX1])([#6])([#6])[#6]}</td></tr>
 * <tr><td>264</td><td>Het trialkylsilane</td><td>{@code [SiX4]([!#6])([#6])([#6])[#6]}</td></tr>
 * <tr><td>265</td><td>Dihet dialkylsilane</td><td>{@code [SiX4]([!#6])([!#6])([#6])[#6]}</td></tr>
 * <tr><td>266</td><td>Trihet alkylsilane</td><td>{@code [SiX4]([!#6])([!#6])([!#6])[#6]}</td></tr>
 * <tr><td>267</td><td>Silicic acid derivative</td><td>{@code [SiX4]([!#6])([!#6])([!#6])[!#6]}</td></tr>
 * <tr><td>268</td><td>Trialkylborane</td><td>{@code [BX3]([#6])([#6])[#6]}</td></tr>
 * <tr><td>269</td><td>Boric acid derivatives</td><td>{@code [BX3]([!#6])([!#6])[!#6]}</td></tr>
 * <tr><td>270</td><td>Boronic acid derivative</td><td>{@code [BX3]([!#6])([!#6])[!#6]}</td></tr>
 * <tr><td>271</td><td>Borohydride</td><td>{@code [BH1,BH2,BH3,BH4]}</td></tr>
 * <tr><td>272</td><td>Quaternary boron</td><td>{@code [BX4]}</td></tr>
 * <tr><td>273</td><td>Aromatic</td><td>{@code a}</td></tr>
 * <tr><td>274</td><td>Heterocyclic</td><td>{@code [!#6;!R0]}</td></tr>
 * <tr><td>275</td><td>Epoxide</td><td>{@code [OX2r3]1[#6r3][#6r3]1}</td></tr>
 * <tr><td>276</td><td>NH aziridine</td><td>{@code [NX3H1r3]1[#6r3][#6r3]1}</td></tr>
 * <tr><td>277</td><td>Spiro</td><td>{@code [D4R;$(*(@*)(@*)(@*)@*)]}</td></tr>
 * <tr><td>278</td><td>Annelated rings</td><td>{@code [R;$(*(@*)(@*)@*);!$([R2;$(*(@*)(@*)(@*)@*)])]@[R;$(*(@*)(@*)@*);!$([R2;$(*(@*)(@*)(@*)@*)])]}</td></tr>
 * <tr><td>279</td><td>Bridged rings</td><td>{@code [R;$(*(@*)(@*)@*);!$([D4R;$(*(@*)(@*)(@*)@*)]);!$([R;$(*(@*)(@*)@*);!$([R2;$(*(@*)(@*)(@*)@*)])]@[R;$(*(@*)(@*)@*);!$([R2;$(*(@*)(@*)(@*)@*)])])]}</td></tr>
 * <tr><td>280</td><td>Sugar pattern 1</td><td>{@code [OX2;$([r5]1@C@C@C(O)@C1),$([r6]1@C@C@C(O)@C(O)@C1)]}</td></tr>
 * <tr><td>281</td><td>Sugar pattern 2</td><td>{@code [OX2;$([r5]1@C(!@[OX2,NX3,SX2,FX1,ClX1,BrX1,IX1])@C@C@C1),$([r6]1@C(!@[OX2,NX3,SX2,FX1,ClX1,BrX1,IX1])@C@C@C@C1)]}</td></tr>
 * <tr><td>282</td><td>Sugar pattern combi</td><td>{@code [OX2;$([r5]1@C(!@[OX2,NX3,SX2,FX1,ClX1,BrX1,IX1])@C@C(O)@C1),$([r6]1@C(!@[OX2,NX3,SX2,FX1,ClX1,BrX1,IX1])@C@C(O)@C(O)@C1)]}</td></tr>
 * <tr><td>283</td><td>Sugar pattern 2 reducing</td><td>{@code [OX2;$([r5]1@C(!@[OX2H1])@C@C@C1),$([r6]1@C(!@[OX2H1])@C@C@C@C1)]}</td></tr>
 * <tr><td>284</td><td>Sugar pattern 2 alpha</td><td>{@code [OX2;$([r5]1@[C@@](!@[OX2,NX3,SX2,FX1,ClX1,BrX1,IX1])@C@C@C1),$([r6]1@[C@@](!@[OX2,NX3,SX2,FX1,ClX1,BrX1,IX1])@C@C@C@C1)]}</td></tr>
 * <tr><td>285</td><td>Sugar pattern 2 beta</td><td>{@code [OX2;$([r5]1@[C@](!@[OX2,NX3,SX2,FX1,ClX1,BrX1,IX1])@C@C@C1),$([r6]1@[C@](!@[OX2,NX3,SX2,FX1,ClX1,BrX1,IX1])@C@C@C@C1)]}</td></tr>
 * <tr><td>286</td><td>Conjugated double bond</td><td>{@code *=*[*]=,#,:[*]}</td></tr>
 * <tr><td>287</td><td>Conjugated tripple bond</td><td>{@code *#*[*]=,#,:[*]}</td></tr>
 * <tr><td>288</td><td>Cis double bond</td><td>{@code *&#47[D2]=[D2]/*}</td></tr>
 * <tr><td>289</td><td>Trans double bond</td><td>{@code *&#47[D2]=[D2]/*}</td></tr>
 * <tr><td>290</td><td>Mixed anhydrides</td><td>{@code [$(*=O),$([#16,#14,#5]),$([#7]([#6]=[OX1]))][#8X2][$(*=O),$([#16,#14,#5]),$([#7]([#6]=[OX1]))]}</td></tr>
 * <tr><td>291</td><td>Halogen on hetero</td><td>{@code [FX1,ClX1,BrX1,IX1][!#6]}</td></tr>
 * <tr><td>292</td><td>Halogen multi subst</td><td>{@code [F,Cl,Br,I;!$([X1]);!$([X0-])]}</td></tr>
 * <tr><td>293</td><td>Trifluoromethyl</td><td>{@code [FX1][CX4;!$([H0][Cl,Br,I]);!$([F][C]([F])([F])[F])]([FX1])([FX1])}</td></tr>
 * <tr><td>294</td><td>C ONS bond</td><td>{@code [#6]~[#7,#8,#16]}</td></tr>
 * <tr><td>295</td><td>Charged</td><td>{@code [!+0]}</td></tr>
 * <tr><td>296</td><td>Anion</td><td>{@code [-1,-2,-3,-4,-5,-6,-7]}</td></tr>
 * <tr><td>297</td><td>Kation</td><td>{@code [+1,+2,+3,+4,+5,+6,+7]}</td></tr>
 * <tr><td>298</td><td>Salt</td><td>{@code ([-1,-2,-3,-4,-5,-6,-7]).([+1,+2,+3,+4,+5,+6,+7])}</td></tr>
 * <tr><td>299</td><td>1,3-Tautomerizable</td><td>{@code [$([#7X2,OX1,SX1]=*[!H0;!$([a;!n])]),$([#7X3,OX2,SX2;!H0]*=*),$([#7X3,OX2,SX2;!H0]*:n)]}</td></tr>
 * <tr><td>300</td><td>1,5-Tautomerizable</td><td>{@code [$([#7X2,OX1,SX1]=,:**=,:*[!H0;!$([a;!n])]),$([#7X3,OX2,SX2;!H0]*=**=*),$([#7X3,OX2,SX2;!H0]*=,:**:n)]}</td></tr>
 * <tr><td>301</td><td>Rotatable bond</td><td>{@code [!$(*#*)&!D1]-!@[!$(*#*)&!D1]}</td></tr>
 * <tr><td>302</td><td>Michael acceptor</td><td>{@code [CX3]=[CX3][$([CX3]=[O,N,S]),$(C#[N]),$([S,P]=[OX1]),$([NX3]=O),$([NX3+](=O)[O-])]}</td></tr>
 * <tr><td>303</td><td>Dicarbodiazene</td><td>{@code [CX3](=[OX1])[NX2]=[NX2][CX3](=[OX1])}</td></tr>
 * <tr><td>304</td><td>CH-acidic</td><td>{@code [$([CX4;!$([H0]);!$(C[!#6;!$([P,S]=O);!$(N(~O)~O)])][$([CX3]=[O,N,S]),$(C#[N]),$([S,P]=[OX1]),$([NX3]=O),$([NX3+](=O)[O-]);!$(*[S,O,N;H1,H2]);!$([*+0][S,O;X1-])]),$([CX4;!$([H0])]1[CX3]=[CX3][CX3]=[CX3]1)]}</td></tr>
 * <tr><td>305</td><td>CH-acidic strong</td><td>{@code [CX4;!$([H0]);!$(C[!#6;!$([P,S]=O);!$(N(~O)~O)])]([$([CX3]=[O,N,S]),$(C#[N]),$([S,P]=[OX1]),$([NX3]=O),$([NX3+](=O)[O-]);!$(*[S,O,N;H1,H2]);!$([*+0][S,O;X1-])])[$([CX3]=[O,N,S]),$(C#[N]),$([S,P]=[OX1]),$([NX3]=O),$([NX3+](=O)[O-]);!$(*[S,O,N;H1,H2]);!$([*+0][S,O;X1-])]}</td></tr>
 * <tr><td>306</td><td>Chiral center specified</td><td>{@code [$([*@](~*)(~*)(*)*),$([*@H](*)(*)*),$([*@](~*)(*)*),$([*@H](~*)~*)]}</td></tr>
 * </tbody></table>
 *
 *
 * @author       egonw
 * @cdk.created  2005-12-30
 *
 * @cdk.keyword  fingerprint
 * @cdk.keyword  similarity
 *
 */


public class SubstructureFingerprinter extends AbstractFingerprinter implements IFingerprinter {

    private static final class Key {
        final String smarts;
        final Pattern pattern;

        public Key(String smarts, Pattern pattern) {
            this.smarts = smarts;
            this.pattern = pattern;
        }
    }

    private final List<Key> keys = new ArrayList<>();

    /**
     * Set up the fingerprinter to use a user-defined set of fragments.
     *
     * @param smarts The collection of fragments to look for
     */
    public SubstructureFingerprinter(String[] smarts) {
        setSmarts(smarts);
    }

    /**
     * Set up the fingerprinter to use the fragments from
     * {@link org.openscience.cdk.fingerprint.StandardSubstructureSets}.
     */
    public SubstructureFingerprinter() {
        try {
            setSmarts(StandardSubstructureSets.getFunctionalGroupSMARTS());
        } catch (Exception ex) {
            throw new IllegalStateException("Could not load SMARTS patterns", ex);
        }
    }

    /**
     * Set the SMARTS patterns.
     * @param smarts the SMARTS
     */
    private void setSmarts(String[] smarts) {
        keys.clear();
        for (String key : smarts) {
            QueryAtomContainer qmol = new QueryAtomContainer(null);
            SmartsPattern ptrn;
            ptrn = SmartsPattern.create(key);
            ptrn.setPrepare(false); // prepare is done once
            keys.add(new Key(key, ptrn));
        }
    }

    /** {@inheritDoc} */
    @Override
    public IBitFingerprint getBitFingerprint(IAtomContainer atomContainer) throws CDKException {
        if (keys.isEmpty()) {
            throw new CDKException("No substructures were defined");
        }

        SmartsPattern.prepare(atomContainer);
        BitSet fingerPrint = new BitSet(keys.size());
        for (int i = 0; i < keys.size(); i++) {
            if (keys.get(i).pattern.matches(atomContainer))
                fingerPrint.set(i, true);
        }
        return new BitSetFingerprint(fingerPrint);
    }


    /** {@inheritDoc} */
    @Override
    public ICountFingerprint getCountFingerprint(IAtomContainer atomContainer) throws CDKException {
        if (keys.isEmpty()) {
            throw new CDKException("No substructures were defined");
        }

        // init SMARTS invariants (connectivity, degree, etc)
        SmartsPattern.prepare(atomContainer);

        final Map<Integer, Integer> map = new TreeMap<>();
        for (int i = 0; i < keys.size(); i++) {
            Pattern ptrn = keys.get(i).pattern;
            int count = ptrn.matchAll(atomContainer)
                            .countUnique();
            map.put(i, count);
        }

        final int length = keys.size();
        final int size = map.size();
        final int[] hash = new int[size], count = new int[size];
        int n = 0;
        for (int h : map.keySet()) {
            hash[n] = h;
            count[n++] = map.get(h);
        }

        return new ICountFingerprint() {

            @Override
            public long size() { return length; }

            @Override
            public int numOfPopulatedbins() { return size; }

            @Override
            public int getCount(int index) { return count[index]; }

            @Override
            public int getHash(int index) { return hash[index]; }

            @Override
            public void merge(ICountFingerprint fp) {}

            @Override
            public void setBehaveAsBitFingerprint(boolean behaveAsBitFingerprint) {}

            @Override
            public boolean hasHash(int hash) { return map.containsKey(hash); }

            @Override
            public int getCountForHash(int hash) { return map.get(hash); }
        };
    }

    /** {@inheritDoc} */
    @Override
    public Map<String, Integer> getRawFingerprint(IAtomContainer iAtomContainer) throws CDKException {
        throw new UnsupportedOperationException();
    }

    /** {@inheritDoc} */
    @Override
    public int getSize() {
        return keys.size();
    }

    /**
     * Retrieves the SMARTS representation of a substructure for a given
     * bit in the fingerprint.
     *
     * @param bitIndex
     * @return SMARTS representation of substructure at
     *         index <code>bitIndex</code>.
     */
    public String getSubstructure(int bitIndex) {
        return keys.get(bitIndex).smarts;
    }
}
