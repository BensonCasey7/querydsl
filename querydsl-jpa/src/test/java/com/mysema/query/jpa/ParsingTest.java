/*
 * Copyright (c) 2010 Mysema Ltd.
 * All rights reserved.
 *
 */
package com.mysema.query.jpa;

import static com.mysema.query.alias.Alias.$;
import static com.mysema.query.alias.Alias.alias;
import static com.mysema.query.jpa.JPQLGrammar.all;
import static com.mysema.query.jpa.JPQLGrammar.some;
import static com.mysema.query.jpa.JPQLGrammar.sum;
import static org.junit.Assert.assertEquals;

import org.junit.Ignore;
import org.junit.Test;

import antlr.RecognitionException;
import antlr.TokenStreamException;

import com.mysema.query.jpa.domain.*;
import com.mysema.query.types.expr.ComparableExpression;
import com.mysema.query.types.expr.DateExpression;
import com.mysema.query.types.expr.NumberExpression;

/**
 * ParsingTest provides.
 *
 * @author tiwe
 * @version $Id$
 */
public class ParsingTest extends AbstractQueryTest{

    @Test
    @Ignore
    public void ArrayExpr() throws Exception {
        query().from(ord).where(ord.items(0).id.eq(1234l)).parse();
    }

    @Test
    public void Basic() throws RecognitionException, TokenStreamException{
        query().from(cat, fatcat).select(cat.name, fatcat.name).parse();
    }

    @Test
    public void BeforeAndAfter() throws RecognitionException,
            TokenStreamException {

        ComparableExpression<java.util.Date> ed = catalog.effectiveDate;
        query().from(catalog).where(ed.gt(DateExpression.currentDate()), ed.goe(DateExpression.currentDate()),
                ed.lt(DateExpression.currentDate()), ed.loe(DateExpression.currentDate())).select(catalog)
                .parse();
    }

    @Test
    public void ComplexConstructor() throws Exception {
        query().from(bar).select(new QFooDTO(bar.count())).parse();
    }

    @Test
    public void DocoExamples910() throws Exception {
        query().from(cat)
               .groupBy(cat.color)
               .select(cat.color, sum(cat.weight), cat.count()).parse();
    }

    @Test
    public void DocoExamples910_2() throws Exception {
        query().from(cat)
               .groupBy(cat.color)
               .having(cat.color.in(Color.TABBY, Color.BLACK))
               .select(cat.color, sum(cat.weight), cat.count()).parse();
    }

    @Test
    @Ignore
    public void DocoExamples910_3() throws Exception {
        query().from(cat).join(cat.kittens, kitten)
               .groupBy(cat)
               .having(kitten.weight.avg().gt(100.0))
               .orderBy(kitten.count().asc(), sum(kitten.weight).desc())
               .select(cat)
               .parse();
    }

    @Test
    public void DocoExamples911() throws Exception {
        query().from(fatcat).where(
                fatcat.weight.gt(sub().from(cat).unique(cat.weight.avg())))
                .parse();

        query().from(cat).where(
                cat.name.eq(some(sub().from(name).list(name.nickName))))
                .parse();

        query().from(cat).where(
                sub().from(mate).where(mate.mate.eq(cat)).list(mate).notExists())
                .parse();

        query().from(cat).where(
                sub().from(mate).where(mate.mate.eq(cat)).exists())
                .parse();

        query().from(cat).where(
                cat.name.notIn(sub().from(name).list(name.nickName)))
                .parse();
    }

    @Test
    public void DocoExamples912() throws Exception {
        query().from(ord, cust)
                .join(ord.lineItems, item).join(item.product, product)
                .from(catalog).join(catalog.prices, price).where(
                        ord.paid.not().and(ord.customer.eq(cust)).and(
                                price.product.eq(product)).and(
                                catalog.effectiveDate.gt(DateExpression.currentDate())).and(
                                catalog.effectiveDate.gt(all(
                                        sub().from(catalog).where(
                                                catalog.effectiveDate.lt(DateExpression.currentDate()))
                                             .list(catalog.effectiveDate)))))
                .groupBy(ord).having(sum(price.amount).gt(0l))
                .orderBy(sum(price.amount).desc())
                .select(ord.id, sum(price.amount), item.count());

        Customer c1 = new Customer();
        Catalog c2 = new Catalog();

        query().from(ord)
               .join(ord.lineItems, item).join(item.product, product)
               .from(catalog).join(catalog.prices, price).where(
                        ord.paid.not().and(ord.customer.eq(c1)).and(
                                price.product.eq(product)).and(catalog.eq(c2)))
                .groupBy(ord).having(sum(price.amount).gt(0l))
                .orderBy(sum(price.amount).desc())
                .select(ord.id, sum(price.amount), item.count());

    }

    @Test
    public void DocoExamples92() throws Exception {
        query().from(cat).parse();

        query().from(cat).parse();

        query().from(cat).parse();

        query().from(form, param).parse();

        query().from(form, param).parse();
    }

    @Test
    public void DocoExamples93() throws Exception {

        query().from(cat).innerJoin(cat.mate, mate).leftJoin(cat.kittens, kitten).parse();

        query().from(cat).leftJoin(cat.mate.kittens, kitten).parse();

        query().from(cat).join(cat.mate, mate).leftJoin(cat.kittens, kitten).parse();

        query().from(cat).innerJoin(cat.mate, mate).leftJoin(cat.kittens, kitten).parse();
    }

    @Test
    public void DocoExamples93_viaAlias() throws Exception {
        Cat c = alias(Cat.class, "cat");
        Cat k = alias(Cat.class, "kittens");
        Cat m = alias(Cat.class, "mate");

        query().from($(c)).innerJoin($(c.getMate()),$(m)).leftJoin($(c.getKittens()),$(k)).parse();

        query().from($(c)).leftJoin($(c.getMate().getKittens()),$(k)).parse();

        query().from($(c)).innerJoin($(c.getMate()),$(m)).leftJoin($(c.getKittens()),$(k)).parse();

        query().from($(c)).innerJoin($(c.getMate()),$(m)).leftJoin($(c.getKittens()),$(k)).parse();
    }

    @Test
    public void DocoExamples94() throws Exception {
        query().from(cat).innerJoin(cat.mate, mate).select(cat.mate).parse();

        query().from(cat).select(cat.mate).parse();

        query().from(cat).select(cat.kittens).parse();

        query().from(cust).select(cust.name.firstName).parse();

        query().from(mother)
            .innerJoin(mother.mate, mate)
            .leftJoin(mother.kittens, offspr)
            .select(mother, offspr, mate).parse();

        query().from(mother)
            .innerJoin(mother.mate, mate)
            .leftJoin(mother.kittens, kitten)
            .select(new QFamily(mother, mate, kitten)).parse();
    }

    @Test
    public void DocoExamples95() throws Exception {
        query().from(cat)
               .select(cat.weight.avg(), cat.weight.sum(), cat.weight.max(), cat.count())
               .parse();
    }

    @Test
    public void DocoExamples96() throws Exception {
        query().from(cat).parse();

        query().from(m, n).where(n.name.eq(m.name)).parse();
    }

    @Test
    public void DocoExamples97() throws Exception {
        query().from(foo, bar).where(foo.startDate.eq(bar.date)).select(foo).parse();

        query().from(cat).where(cat.mate.name.isNotNull()).parse();

        query().from(cat, rival).where(cat.mate.eq(rival.mate)).parse();

        query().from(cat, mate).where(cat.mate.eq(mate)).select(cat, mate).parse();

        query().from(cat).where(cat.id.eq(123)).parse();

        query().from(cat).where(cat.mate.id.eq(69)).parse();

        query().from(person).where(
                person.pid.country.eq("AU").and(
                        person.pid.medicareNumber.eq(123456))).parse();

        query().from(account).where(account.owner.pid.medicareNumber.eq(123456)).parse();

        query().from(cat).where(cat.instanceOf(DomesticCat.class)).parse();

        query().from(log, payment).where(
                log.item.instanceOf(Payment.class),
                log.item.id.eq(payment.id)).parse();
    }

    @Test
    @Ignore
    public void DocoExamples98() throws Exception {
        query().from(cat).where(cat.name.between("A", "B")).parse();

        query().from(cat).where(cat.name.in("Foo", "Bar", "Baz")).parse();

        query().from(cat).where(cat.name.notBetween("A", "B")).parse();

        query().from(cat).where(cat.name.notIn("Foo", "Bar", "Baz")).parse();

        query().from(cat).where(cat.kittens.size().gt(0)).parse();

        query().from(cat).where(cat.kittens.size().gt(0)).parse();

        query().select(mother).from(mother, kit).where(kit.in(mother.kittens)).parse();

        query().select(p).from(list, p).where(p.name.eq(some(list.names))).parse();

        query().from(cat).where(cat.kittens.isNotEmpty()).parse();

        query().select(person).from(person, calendar).where(
                calendar.holidays("national holiday").eq(person.birthDay).and(
                        person.nationality.calendar.eq(calendar))).parse();

        query().select(item).from(item, ord).where(
                ord.items(ord.deliveredItemIndices(0)).eq(item).and(
                        ord.id.eq(1l))).parse();

        query().select(item).from(item, ord).where(
                ord.items(ord.items.size().subtract(1)).eq(item))
                .parse();

        query()
                .select(cust)
                .from(prod, store)
                .innerJoin(store.customers, cust)
                .where(
                        prod.name.eq("widget").and(
                                store.location.name.in("Melbourne", "Sydney"))
                                .and(prod.eq(all(cust.currentOrder.lineItems))))
                .parse();

        prod.eq(new Product());
        prod.eq(new QProduct("p"));
        prod.eq(new QItem("p"));

    }

    @Test
    public void DocoExamples99() throws Exception {
        query().from(cat).orderBy(cat.name.asc(), cat.weight.desc(),
                cat.birthdate.asc()).parse();
    }

    @Test
    public void DoubleLiteral() throws Exception {
        query().from(cat).where(cat.weight.lt((int) 3.1415)).parse();

        query().from(cat).where(cat.weight.gt((int) 3.1415e3)).parse();
    }

    @Test
    public void Fetch() throws RecognitionException, TokenStreamException{
        query().from(cat).innerJoin(cat.mate, mate).fetch().parse();

        query().from(cat).innerJoin(cat.mate, mate).fetch().fetch().parse();
    }

    @Test
    public void inNotIn() throws Exception {
        query().from(foo).where(foo.bar.in("a", "b", "c")).parse();

        query().from(foo).where(foo.bar.notIn("a", "b", "c")).parse();
    }

    @Test
    public void joinFlags1() throws RecognitionException, TokenStreamException{
        query().from(cat).fetchAll().parse();
    }

    @Test
    public void joinFlags2() throws RecognitionException, TokenStreamException{
        query().from(cat).fetchAll().from(cat1).fetchAll().parse();
    }

    @Test
    public void joinFlags3() throws RecognitionException, TokenStreamException{
        query().from(cat).fetchAll().from(cat1).fetchAll().parse();
    }

    @Test
    public void joins() throws RecognitionException, TokenStreamException{
        query().from(cat).join(cat.mate).select(cat).parse();

        query().from(cat).innerJoin(cat.mate).select(cat).parse();

        query().from(cat).leftJoin(cat.mate).select(cat).parse();
    }
    
    @Test
    public void joins2() throws RecognitionException, TokenStreamException{
        query().from(cat).join(cat.mate, mate).with(mate.name.eq("Bob")).parse();
    }

    @Test
    public void multipleFromClasses() throws Exception {
        query().from(qat, foo).parse();
    }

    @Test
    public void serialization(){
        QueryHelper query = query();

        query.from(cat);
        assertEquals("from Cat cat", query.toString());

        query.from(fatcat);
        assertEquals("from Cat cat, Cat fatcat", query.toString());
    }

    @Test
    public void Casts() throws Exception {
        NumberExpression<Double> bw = cat.bodyWeight;
        query().from(cat).select(bw.byteValue(), bw.doubleValue(), bw.floatValue(),
                bw.intValue(), bw.longValue(), bw.shortValue(),
                bw.stringValue()).parse();

        query().from(cat).select(bw.castToNum(Byte.class)).parse();
    }

    @Test
    public void GroupBy() throws Exception {
        query().from(qat).groupBy(qat.breed).parse();

        query().from(qat).groupBy(qat.breed, qat.eyecolor).parse();
    }

    @Test
    public void Not() throws Exception {
        query().from(cat).where(cat.kittens.size().lt(1).not()).parse();

        query().from(cat).where(cat.kittens.size().gt(1).not()).parse();

        query().from(cat).where(cat.kittens.size().goe(1).not()).parse();

        query().from(cat).where(cat.kittens.size().loe(1).not()).parse();

        query().from(cat).where(cat.name.between("A", "B").not()).parse();

        query().from(cat).where(cat.name.notBetween("A", "B").not()).parse();

        query().from(cat).where(cat.kittens.size().loe(1).not().not()).parse();

        query().from(cat).where(cat.kittens.size().loe(1).not().not().not()).parse();
    }

    @Test
    public void OrderBy() throws Exception {
        query().from(qat).orderBy(qat.toes.avg().asc()).parse();

        query().from(an).orderBy(an.bodyWeight.sqrt().divide(2.0).asc()).parse();
    }

    @Test
    public void Select() throws Exception {
//        query().select(Ops.AggOps.COUNT_ALL_AGG_EXPR).from(qat).parse();

        query().from(qat).select(qat.weight.avg()).parse();
    }

    @Test
    public void Sum() throws RecognitionException, TokenStreamException {
        query().from(cat).select(sum(cat.kittens.size())).parse();

        query().from(cat).where(sum(cat.kittens.size()).gt(0)).select(cat).parse();

        query().from(cat).where(cat.kittens.isEmpty()).select(cat).parse();

        query().from(cat).where(cat.kittens.isNotEmpty()).select(cat).parse();

//        query().from(cat)
//         .groupBy(cat.name)
//         .having(sum(cat.bodyWeight).gt(0))
//         .select(cat).parse();
    }

    @Test
    public void Where() throws Exception {

        query().from(qat).where(qat.name.in("crater", "bean", "fluffy")).parse();

        query().from(qat).where(qat.name.notIn("crater", "bean", "fluffy")).parse();

        query().from(an).where(an.bodyWeight.sqrt().gt(10.0)).parse();

        query().from(an).where(an.bodyWeight.sqrt().divide(2d).gt(10.0)).parse();

        query().from(an).where(
                an.bodyWeight.gt(10).and(
                        an.bodyWeight.lt(100).or(an.bodyWeight.isNull())))
                .parse();
    }

}
